package ch.motherheart.examples.jooq.shop.order.service;

import ch.motherheart.examples.jooq.heart.tables.records.PurchaseOrderRecord;
import ch.motherheart.examples.jooq.shop.common.DomainRejectedException;
import ch.motherheart.examples.jooq.shop.customer.service.CustomerService;
import ch.motherheart.examples.jooq.shop.order.dto.OrderSummary;
import ch.motherheart.examples.jooq.shop.order.dto.PlaceOrderRequest;
import ch.motherheart.examples.jooq.shop.order.dto.PlacedOrder;
import ch.motherheart.examples.jooq.shop.order.persistence.OrderRepository;
import ch.motherheart.examples.jooq.shop.product.service.ProductService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Gathers what it needs from other domains through their services (service → service),
 * never through their repositories or records.
 */
@ApplicationScoped
@Transactional
public class OrderService {

    private final OrderRepository repository;
    private final CustomerService customers;
    private final ProductService products;

    OrderService(OrderRepository repository, CustomerService customers, ProductService products) {
        this.repository = repository;
        this.customers = customers;
        this.products = products;
    }

    public PlacedOrder place(PlaceOrderRequest request) {
        if (!customers.exists(request.customerId())) {
            throw new DomainRejectedException("unknown customer " + request.customerId());
        }
        PurchaseOrderRecord order = repository.insertOrder(
                request.customerId(), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        BigDecimal total = BigDecimal.ZERO;
        for (PlaceOrderRequest.Line line : request.lines()) {
            BigDecimal price = products.currentPrice(line.productId())
                    .orElseThrow(() -> new DomainRejectedException("unknown product " + line.productId()));
            repository.insertLine(order.getId(), line.productId(), line.quantity(), price);
            total = total.add(price.multiply(BigDecimal.valueOf(line.quantity())));
        }
        return new PlacedOrder(order.getId(), order.getCustomerId(), order.getOrderedAt(), total);
    }

    public List<OrderSummary> summariesFor(long customerId) {
        return repository.summariesFor(customerId);
    }
}
