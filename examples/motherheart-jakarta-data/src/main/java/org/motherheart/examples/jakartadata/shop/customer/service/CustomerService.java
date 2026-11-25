package org.motherheart.examples.jakartadata.shop.customer.service;

import org.motherheart.examples.jakartadata.shop.common.DomainNotFoundException;
import org.motherheart.examples.jakartadata.shop.customer.dto.CustomerListItem;
import org.motherheart.examples.jakartadata.shop.customer.dto.CustomerRequest;
import org.motherheart.examples.jakartadata.shop.customer.dto.CustomerView;
import org.motherheart.examples.jakartadata.shop.customer.persistence.Customer;
import org.motherheart.examples.jakartadata.shop.customer.persistence.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

/** The only door into the customer domain. */
@ApplicationScoped
@Transactional
public class CustomerService {

    private final CustomerRepository repository;

    CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public CustomerView create(CustomerRequest request) {
        return view(repository.insert(new Customer(request.name(), request.email())));
    }

    public CustomerView get(long id) {
        return view(load(id));
    }

    public CustomerView update(long id, CustomerRequest request) {
        Customer customer = load(id);
        customer.change(request.name(), request.email());
        return view(repository.update(customer));
    }

    public void delete(long id) {
        repository.delete(load(id));
    }

    public List<CustomerListItem> list() {
        return repository.list();
    }

    /** Exposed to other domains: they ask, they never look inside. */
    public boolean exists(long id) {
        return repository.countById(id) > 0;
    }

    private Customer load(long id) {
        return repository.findById(id).orElseThrow(() -> new DomainNotFoundException("customer", id));
    }

    private static CustomerView view(Customer customer) {
        return new CustomerView(customer.getId(), customer.getName(), customer.getEmail());
    }
}
