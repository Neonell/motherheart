package org.motherheart.examples.jakartadata.shop.product.persistence;

import org.motherheart.examples.jakartadata.shop.product.dto.ProductListItem;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** CRUD on the entity from {@link CrudRepository}; lists and single values as validated queries. */
@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    @Query("""
            select new org.motherheart.examples.jakartadata.shop.product.dto.ProductListItem(p.id, p.name, p.price)
            from Product p
            where p.price <= :maxPrice
            order by p.price, p.name""")
    List<ProductListItem> listUpTo(BigDecimal maxPrice);

    /** One column, not a whole entity: do not load what you do not need (Motherheart rule 4). */
    @Query("select p.price from Product p where p.id = :id")
    Optional<BigDecimal> priceOf(Long id);
}
