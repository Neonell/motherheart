package ch.motherheart.examples.jakartadata.shop.customer.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Entity: used for CRUD only (Motherheart rule 1). Never returned in a list. */
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    protected Customer() {
        // for Hibernate
    }

    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void change(String newName, String newEmail) {
        this.name = newName;
        this.email = newEmail;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
