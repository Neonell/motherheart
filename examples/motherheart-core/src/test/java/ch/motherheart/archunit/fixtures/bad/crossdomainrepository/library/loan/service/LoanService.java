package ch.motherheart.archunit.fixtures.bad.crossdomainrepository.library.loan.service;

import ch.motherheart.archunit.fixtures.bad.crossdomainrepository.library.member.persistence.MemberRepository;

public class LoanService {
    MemberRepository members;

    boolean canLoan(Long memberId) {
        return members.existsById(memberId);
    }
}
