package org.motherheart.examples.jooq.shop.product.persistence;

import static org.motherheart.examples.jooq.heart.Tables.PRODUCT;

import org.motherheart.examples.jooq.heart.tables.records.ProductRecord;
import org.motherheart.examples.jooq.shop.product.dto.ProductListItem;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.jooq.Records;

/** CRUD on the generated {@link ProductRecord}, and reads in SQL checked against the generated heart. */
@ApplicationScoped
public class ProductRepository {

    private final DSLContext dsl;

    ProductRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Optional<ProductRecord> findById(long id) {
        return dsl.fetchOptional(PRODUCT, PRODUCT.ID.eq(id));
    }

    public ProductRecord insert(String name, BigDecimal price) {
        ProductRecord product = dsl.newRecord(PRODUCT);
        product.setName(name);
        product.setPrice(price);
        product.store();
        return product;
    }

    public ProductRecord update(ProductRecord product) {
        product.update();
        return product;
    }

    public void delete(ProductRecord product) {
        product.delete();
    }

    public List<ProductListItem> listUpTo(BigDecimal maxPrice) {
        return dsl.select(PRODUCT.ID, PRODUCT.NAME, PRODUCT.PRICE)
                .from(PRODUCT)
                .where(PRODUCT.PRICE.le(maxPrice))
                .orderBy(PRODUCT.PRICE, PRODUCT.NAME)
                .fetch(Records.mapping(ProductListItem::new));
    }

    /** One column, not a whole record: do not load what you do not need (Motherheart rule 4). */
    public Optional<BigDecimal> priceOf(long id) {
        return dsl.select(PRODUCT.PRICE)
                .from(PRODUCT)
                .where(PRODUCT.ID.eq(id))
                .fetchOptional(PRODUCT.PRICE);
    }
}
