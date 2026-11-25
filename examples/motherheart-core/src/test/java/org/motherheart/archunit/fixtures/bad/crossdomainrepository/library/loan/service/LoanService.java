package org.motherheart.archunit.fixtures.bad.crossdomainrepository.library.loan.service;

import org.motherheart.archunit.fixtures.bad.crossdomainrepository.library.member.persistence.MemberRepository;

public class LoanService {
    MemberRepository members;

    boolean canLoan(Long memberId) {
        return members.existsById(memberId);
    }
}
