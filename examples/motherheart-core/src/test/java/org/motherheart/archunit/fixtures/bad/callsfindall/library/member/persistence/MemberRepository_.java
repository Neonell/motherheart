package org.motherheart.archunit.fixtures.bad.callsfindall.library.member.persistence;

import java.util.List;

/** Simulates the generated implementation: here the type is concrete. */
public class MemberRepository_ implements MemberRepository {
    @Override
    public List<Member> findAll() {
        return List.of();
    }
}
