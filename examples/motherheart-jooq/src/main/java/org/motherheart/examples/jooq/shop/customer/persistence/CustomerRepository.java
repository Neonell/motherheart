package ch.motherheart.examples.jooq.shop.customer.persistence;

import static ch.motherheart.examples.jooq.heart.Tables.CUSTOMER;

import ch.motherheart.examples.jooq.heart.tables.records.CustomerRecord;
import ch.motherheart.examples.jooq.shop.customer.dto.CustomerListItem;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.jooq.Records;

/**
 * The customer persistence, in one place: CRUD on the generated {@link CustomerRecord}, and lists in
 * SQL checked by the compiler against the generated heart. Split it when it grows too big.
 */
@ApplicationScoped
public class CustomerRepository {

    private final DSLContext dsl;

    CustomerRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Optional<CustomerRecord> findById(long id) {
        return dsl.fetchOptional(CUSTOMER, CUSTOMER.ID.eq(id));
    }

    public CustomerRecord insert(String name, String email) {
        CustomerRecord customer = dsl.newRecord(CUSTOMER);
        customer.setName(name);
        customer.setEmail(email);
        customer.store();
        return customer;
    }

    public CustomerRecord update(CustomerRecord customer) {
        customer.update();
        return customer;
    }

    public void delete(CustomerRecord customer) {
        customer.delete();
    }

    public List<CustomerListItem> list() {
        return dsl.select(CUSTOMER.ID, CUSTOMER.NAME)
                .from(CUSTOMER)
                .orderBy(CUSTOMER.NAME)
                .fetch(Records.mapping(CustomerListItem::new));
    }

    public boolean exists(long id) {
        return dsl.fetchExists(CUSTOMER, CUSTOMER.ID.eq(id));
    }
}
