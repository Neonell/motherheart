package ch.motherheart.archunit.fixtures.bad.resourceusesrepository.library.member.persistence;

import java.util.Optional;

public interface MemberRepository {
    Optional<Member> findById(Long id);
}
