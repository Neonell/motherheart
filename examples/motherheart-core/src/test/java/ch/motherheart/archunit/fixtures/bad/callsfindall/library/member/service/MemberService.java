package ch.motherheart.archunit.fixtures.bad.callsfindall.library.member.service;

import ch.motherheart.archunit.fixtures.bad.callsfindall.library.member.persistence.MemberRepository;

public class MemberService {
    MemberRepository repository;

    public int count() {
        return repository.findAll().size();
    }
}
