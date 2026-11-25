package org.motherheart.archunit.fixtures.bad.crossdomainrepository.library.member.persistence;

public interface MemberRepository {
    boolean existsById(Long id);
}
