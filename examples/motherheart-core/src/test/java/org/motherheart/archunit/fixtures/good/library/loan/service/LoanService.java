package org.motherheart.archunit.fixtures.good.library.loan.service;

import org.motherheart.archunit.fixtures.good.library.member.service.MemberService;
import org.motherheart.archunit.fixtures.good.library.loan.persistence.LoanRepository;
import org.motherheart.archunit.fixtures.good.library.loan.persistence.Loan;

public class LoanService {
    private final LoanRepository repository;
    private final MemberService members;

    LoanService(LoanRepository repository, MemberService members) {
        this.repository = repository;
        this.members = members;
    }

    public Long place(Long memberId) {
        if (!members.exists(memberId)) {
            throw new IllegalArgumentException("unknown member");
        }
        return repository.insert(new Loan(memberId)).getId();
    }
}
