package ch.motherheart.examples.jooq.shop.order.persistence;

import static ch.motherheart.examples.jooq.heart.Tables.ORDER_LINE;
import static ch.motherheart.examples.jooq.heart.Tables.PURCHASE_ORDER;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.sum;

import ch.motherheart.examples.jooq.heart.tables.records.PurchaseOrderRecord;
import ch.motherheart.examples.jooq.shop.order.dto.OrderSummary;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.Records;

/** Writes orders and their lines, and reads order summaries, in SQL checked against the generated heart. */
@ApplicationScoped
public class OrderRepository {

    private final DSLContext dsl;

    OrderRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public PurchaseOrderRecord insertOrder(long customerId, LocalDateTime orderedAt) {
        PurchaseOrderRecord order = dsl.newRecord(PURCHASE_ORDER);
        order.setCustomerId(customerId);
        order.setOrderedAt(orderedAt);
        order.store();
        return order;
    }

    public void insertLine(long orderId, long productId, int quantity, BigDecimal unitPrice) {
        dsl.insertInto(ORDER_LINE)
                .set(ORDER_LINE.ORDER_ID, orderId)
                .set(ORDER_LINE.PRODUCT_ID, productId)
                .set(ORDER_LINE.QUANTITY, quantity)
                .set(ORDER_LINE.UNIT_PRICE, unitPrice)
                .execute();
    }

    public List<OrderSummary> summariesFor(long customerId) {
        return dsl.select(
                        PURCHASE_ORDER.ID,
                        PURCHASE_ORDER.ORDERED_AT,
                        count(ORDER_LINE.ID).coerce(Long.class),
                        sum(ORDER_LINE.UNIT_PRICE.mul(ORDER_LINE.QUANTITY)))
                .from(PURCHASE_ORDER)
                .join(ORDER_LINE).on(ORDER_LINE.ORDER_ID.eq(PURCHASE_ORDER.ID))
                .where(PURCHASE_ORDER.CUSTOMER_ID.eq(customerId))
                .groupBy(PURCHASE_ORDER.ID, PURCHASE_ORDER.ORDERED_AT)
                .orderBy(PURCHASE_ORDER.ORDERED_AT.desc(), PURCHASE_ORDER.ID.desc())
                .fetch(Records.mapping(OrderSummary::new));
    }
}
