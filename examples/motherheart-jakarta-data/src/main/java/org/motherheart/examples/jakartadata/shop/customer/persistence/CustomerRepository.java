package org.motherheart.examples.jakartadata.shop.customer.persistence;

import org.motherheart.examples.jakartadata.shop.customer.dto.CustomerListItem;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import java.util.List;

/**
 * The customer persistence, in one place: CRUD on the entity comes from {@link CrudRepository},
 * lists are validated queries projected into DTOs. Split it when it grows too big.
 *
 * <p>{@code CrudRepository} also offers {@code findAll()}, a collection of entities: it exists, but
 * calling it fails the build ({@code listsNeverContainEntities}).
 */
@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    @Query("""
            select new org.motherheart.examples.jakartadata.shop.customer.dto.CustomerListItem(c.id, c.name)
            from Customer c
            order by c.name""")
    List<CustomerListItem> list();

    @Query("select count(c) from Customer c where c.id = :id")
    long countById(Long id);
}
