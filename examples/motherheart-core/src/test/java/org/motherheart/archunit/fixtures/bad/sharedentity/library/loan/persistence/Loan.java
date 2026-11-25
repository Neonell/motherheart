package org.motherheart.archunit.fixtures.bad.sharedentity.library.loan.persistence;

import org.motherheart.archunit.fixtures.bad.sharedentity.library.member.persistence.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Loan {
    @Id
    Long id;
    @ManyToOne
    Member member;
}
