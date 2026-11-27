package ch.motherheart.examples.jakartadata.shop.dashboard.persistence;

import ch.motherheart.examples.jakartadata.shop.dashboard.dto.MonthlyRevenue;
import ch.motherheart.examples.jakartadata.shop.dashboard.dto.TopProduct;
import jakarta.data.Limit;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A reporting domain: it reads the heart (the tables) across the other domains, but never
 * touches their Java classes. With Jakarta Data the query still names their entities, so this
 * is where the Jakarta Data flavour is less isolated than the jOOQ one: see the examples README.
 *
 * <p>The year is given as a half-open range so the index on {@code ordered_at} can be used.
 */
@Repository
public interface DashboardRepository {

    @Query("""
            select new ch.motherheart.examples.jakartadata.shop.dashboard.dto.MonthlyRevenue(
                extract(year from o.orderedAt), extract(month from o.orderedAt),
                count(distinct o.id), sum(l.quantity * l.unitPrice))
            from PurchaseOrder o
                join OrderLine l on l.orderId = o.id
            where o.orderedAt >= :from and o.orderedAt < :to
            group by extract(year from o.orderedAt), extract(month from o.orderedAt)
            order by extract(year from o.orderedAt), extract(month from o.orderedAt)""")
    List<MonthlyRevenue> revenueByMonth(LocalDateTime from, LocalDateTime to);

    @Query("""
            select new ch.motherheart.examples.jakartadata.shop.dashboard.dto.MonthlyRevenue(
                extract(year from o.orderedAt), extract(month from o.orderedAt),
                count(distinct o.id), sum(l.quantity * l.unitPrice))
            from PurchaseOrder o
                join OrderLine l on l.orderId = o.id
            where o.orderedAt >= :from and o.orderedAt < :to
            group by extract(year from o.orderedAt), extract(month from o.orderedAt)
            order by extract(year from o.orderedAt), extract(month from o.orderedAt)""")
    List<MonthlyRevenue> revenueByMonthWithPagination(LocalDateTime from, LocalDateTime to, PageRequest pageRequest);

    @Query("""
            select new ch.motherheart.examples.jakartadata.shop.dashboard.dto.TopProduct(
                p.id, p.name, sum(l.quantity), sum(l.quantity * l.unitPrice))
            from OrderLine l
                join PurchaseOrder o on o.id = l.orderId
                join Product p on p.id = l.productId
            where o.orderedAt >= :from and o.orderedAt < :to
            group by p.id, p.name
            order by sum(l.quantity * l.unitPrice) desc, p.name""")
    List<TopProduct> topProducts(LocalDateTime from, LocalDateTime to, Limit limit);
}
