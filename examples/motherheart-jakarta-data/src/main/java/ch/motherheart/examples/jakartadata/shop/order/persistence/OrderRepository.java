package ch.motherheart.examples.jakartadata.shop.order.persistence;

import ch.motherheart.examples.jakartadata.shop.order.dto.OrderSummary;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import java.util.List;

/** CRUD on orders from {@link CrudRepository}, plus their lines, plus the order lists. */
@Repository
public interface OrderRepository extends CrudRepository<PurchaseOrder, Long> {

    @Insert
    OrderLine insertLine(OrderLine line);

    /**
     * An explicit join on ids, written as you would write the SQL. The selected columns are mapped
     * to the {@link OrderSummary} record by the return type (no {@code select new}: the package name
     * {@code order} is an HQL keyword).
     */
    @Query("""
            select o.id, o.orderedAt, count(l.id), sum(l.quantity * l.unitPrice)
            from PurchaseOrder o
                join OrderLine l on l.orderId = o.id
            where o.customerId = :customerId
            group by o.id, o.orderedAt
            order by o.orderedAt desc, o.id desc""")
    List<OrderSummary> summariesFor(Long customerId);
}
