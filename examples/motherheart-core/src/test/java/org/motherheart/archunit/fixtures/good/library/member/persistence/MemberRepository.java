package org.motherheart.archunit.fixtures.good.library.member.persistence;

import org.motherheart.archunit.fixtures.good.library.member.dto.MemberListItem;
import java.util.List;
import java.util.Optional;

/** One repository per domain: CRUD on the entity and DTO lists together. */
public interface MemberRepository {
    Optional<Member> findById(Long id);

    Member insert(Member member);

    List<MemberListItem> list();

    long countById(Long id);
}
