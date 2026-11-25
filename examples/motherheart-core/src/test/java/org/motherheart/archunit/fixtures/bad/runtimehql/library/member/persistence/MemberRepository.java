package org.motherheart.archunit.fixtures.bad.runtimehql.library.member.persistence;

import jakarta.persistence.EntityManager;

public class MemberRepository {
    EntityManager entityManager;

    Object count(String table) {
        return entityManager.createQuery("select count(c) from " + table + " c").getSingleResult();
    }
}
