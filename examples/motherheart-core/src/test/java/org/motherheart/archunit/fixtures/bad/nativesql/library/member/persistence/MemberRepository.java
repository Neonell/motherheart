package org.motherheart.archunit.fixtures.bad.nativesql.library.member.persistence;

import jakarta.persistence.EntityManager;

public class MemberRepository {
    EntityManager entityManager;

    Object count() {
        return entityManager.createNativeQuery("select count(*) from member").getSingleResult();
    }
}
