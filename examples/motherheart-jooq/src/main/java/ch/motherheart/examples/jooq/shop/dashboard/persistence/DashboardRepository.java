package ch.motherheart.examples.jooq.shop.dashboard.persistence;

import static ch.motherheart.examples.jooq.heart.Tables.ORDER_LINE;
import static ch.motherheart.examples.jooq.heart.Tables.PRODUCT;
import static ch.motherheart.examples.jooq.heart.Tables.PURCHASE_ORDER;
import static org.jooq.impl.DSL.countDistinct;
import static org.jooq.impl.DSL.extract;
import static org.jooq.impl.DSL.sum;

import ch.motherheart.examples.jooq.shop.dashboard.dto.MonthlyRevenue;
import ch.motherheart.examples.jooq.shop.dashboard.dto.TopProduct;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.Records;

/**
 * A reporting domain: it reads the heart (the tables) across the other domains, but never
 * touches their Java classes. The year is a half-open range so the index on ordered_at is used.
 */
@ApplicationScoped
public class DashboardRepository {

    private static final Field<BigDecimal> LINE_TOTAL = ORDER_LINE.UNIT_PRICE.mul(ORDER_LINE.QUANTITY);

    private final DSLContext dsl;

    DashboardRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<MonthlyRevenue> revenueByMonth(LocalDateTime from, LocalDateTime to) {
        Field<Integer> year = extract(PURCHASE_ORDER.ORDERED_AT, DatePart.YEAR);
        Field<Integer> month = extract(PURCHASE_ORDER.ORDERED_AT, DatePart.MONTH);
        return dsl.select(year, month, countDistinct(PURCHASE_ORDER.ID).coerce(Long.class), sum(LINE_TOTAL))
                .from(PURCHASE_ORDER)
                .join(ORDER_LINE).on(ORDER_LINE.ORDER_ID.eq(PURCHASE_ORDER.ID))
                .where(PURCHASE_ORDER.ORDERED_AT.ge(from).and(PURCHASE_ORDER.ORDERED_AT.lt(to)))
                .groupBy(year, month)
                .orderBy(year, month)
                .fetch(Records.mapping(MonthlyRevenue::new));
    }

    public List<MonthlyRevenue> revenueByMonthsPaginated(LocalDateTime from, LocalDateTime to,
        int pageNumber, int pageSize) {
        Field<Integer> year = extract(PURCHASE_ORDER.ORDERED_AT, DatePart.YEAR);
        Field<Integer> month = extract(PURCHASE_ORDER.ORDERED_AT, DatePart.MONTH);
        return dsl.select(year, month, countDistinct(PURCHASE_ORDER.ID).coerce(Long.class), sum(LINE_TOTAL))
            .from(PURCHASE_ORDER)
            .join(ORDER_LINE).on(ORDER_LINE.ORDER_ID.eq(PURCHASE_ORDER.ID))
            .where(PURCHASE_ORDER.ORDERED_AT.ge(from).and(PURCHASE_ORDER.ORDERED_AT.lt(to)))
            .groupBy(year, month)
            .orderBy(year, month)
            .limit(pageSize)
            .offset((pageNumber - 1) * pageSize)
            .fetch(Records.mapping(MonthlyRevenue::new));
    }

    /**
     * Seek (keyset) pagination: returns the months strictly after {@code (lastYear, lastMonth)}.
     * The seek values follow the {@code orderBy} columns and must form a unique key, otherwise rows
     * sharing the key of the last row of a page are skipped; the GROUP BY makes (year, month) unique.
     * Because of the GROUP BY, jOOQ renders the seek predicate in HAVING.
     */
    public List<MonthlyRevenue> revenueByMonthSeeked(
        LocalDateTime from,
        LocalDateTime to,
        int lastYear,
        int lastMonth,
        int pageSize)
    {
        Field<Integer> year = extract(PURCHASE_ORDER.ORDERED_AT, DatePart.YEAR);
        Field<Integer> month = extract(PURCHASE_ORDER.ORDERED_AT, DatePart.MONTH);
        return dsl.select(year, month, countDistinct(PURCHASE_ORDER.ID).coerce(Long.class), sum(LINE_TOTAL))
            .from(PURCHASE_ORDER)
            .join(ORDER_LINE).on(ORDER_LINE.ORDER_ID.eq(PURCHASE_ORDER.ID))
            .where(PURCHASE_ORDER.ORDERED_AT.ge(from).and(PURCHASE_ORDER.ORDERED_AT.lt(to)))
            .groupBy(year, month)
            .orderBy(year, month)
            .seek(lastYear, lastMonth)
            .limit(pageSize)
            .fetch(Records.mapping(MonthlyRevenue::new));
    }

    public List<TopProduct> topProducts(LocalDateTime from, LocalDateTime to, int limit) {
        Field<BigDecimal> revenue = sum(LINE_TOTAL);
        return dsl.select(PRODUCT.ID, PRODUCT.NAME, sum(ORDER_LINE.QUANTITY).coerce(Long.class), revenue)
                .from(ORDER_LINE)
                .join(PURCHASE_ORDER).on(PURCHASE_ORDER.ID.eq(ORDER_LINE.ORDER_ID))
                .join(PRODUCT).on(PRODUCT.ID.eq(ORDER_LINE.PRODUCT_ID))
                .where(PURCHASE_ORDER.ORDERED_AT.ge(from).and(PURCHASE_ORDER.ORDERED_AT.lt(to)))
                .groupBy(PRODUCT.ID, PRODUCT.NAME)
                .orderBy(revenue.desc(), PRODUCT.NAME)
                .limit(limit)
                .fetch(Records.mapping(TopProduct::new));
    }

}
