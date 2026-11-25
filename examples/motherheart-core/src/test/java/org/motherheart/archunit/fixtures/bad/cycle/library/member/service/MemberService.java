package org.motherheart.archunit.fixtures.bad.cycle.library.member.service;

import org.motherheart.archunit.fixtures.bad.cycle.library.loan.service.LoanService;

public class MemberService {
    LoanService loans;
}
