package org.motherheart.examples.jakartadata.shop.order.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * The customer is referenced by id, not by a {@code @ManyToOne Customer}: no hidden join,
 * no leak of the customer domain. The foreign key in the database guards integrity.
 */
@Entity
@Table(name = "purchase_order")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "ordered_at")
    private LocalDateTime orderedAt;

    protected PurchaseOrder() {
        // for Hibernate
    }

    public PurchaseOrder(Long customerId, LocalDateTime orderedAt) {
        this.customerId = customerId;
        this.orderedAt = orderedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }
}
