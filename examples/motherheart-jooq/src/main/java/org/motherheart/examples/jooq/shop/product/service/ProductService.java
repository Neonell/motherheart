package ch.motherheart.examples.jooq.shop.product.service;

import ch.motherheart.examples.jooq.heart.tables.records.ProductRecord;
import ch.motherheart.examples.jooq.shop.common.DomainNotFoundException;
import ch.motherheart.examples.jooq.shop.product.dto.ProductListItem;
import ch.motherheart.examples.jooq.shop.product.dto.ProductRequest;
import ch.motherheart.examples.jooq.shop.product.dto.ProductView;
import ch.motherheart.examples.jooq.shop.product.persistence.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class ProductService {

    private static final BigDecimal NO_LIMIT = BigDecimal.valueOf(Long.MAX_VALUE);

    private final ProductRepository repository;

    ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public ProductView create(ProductRequest request) {
        return view(repository.insert(request.name(), request.price()));
    }

    public ProductView get(long id) {
        return view(load(id));
    }

    public ProductView update(long id, ProductRequest request) {
        ProductRecord product = load(id);
        product.setName(request.name());
        product.setPrice(request.price());
        return view(repository.update(product));
    }

    public void delete(long id) {
        repository.delete(load(id));
    }

    public List<ProductListItem> list(BigDecimal maxPrice) {
        return repository.listUpTo(maxPrice == null ? NO_LIMIT : maxPrice);
    }

    /** Exposed to other domains: the current price, or empty if the product does not exist. */
    public Optional<BigDecimal> currentPrice(long id) {
        return repository.priceOf(id);
    }

    private ProductRecord load(long id) {
        return repository.findById(id).orElseThrow(() -> new DomainNotFoundException("product", id));
    }

    private static ProductView view(ProductRecord product) {
        return new ProductView(product.getId(), product.getName(), product.getPrice());
    }
}
