package org.motherheart.archunit.fixtures.good.library.loan.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Loan {
    @Id
    Long id;
    /** Another domain is referenced by id, never by object. */
    Long memberId;

    public Loan(Long memberId) {
        this.memberId = memberId;
    }

    public Long getId() {
        return id;
    }
}
