package ch.motherheart.archunit.fixtures.bad.cycle.library.member.service;

import ch.motherheart.archunit.fixtures.bad.cycle.library.loan.service.LoanService;

public class MemberService {
    LoanService loans;
}
