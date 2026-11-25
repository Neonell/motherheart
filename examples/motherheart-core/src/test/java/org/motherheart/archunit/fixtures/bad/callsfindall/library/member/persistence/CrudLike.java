package org.motherheart.archunit.fixtures.bad.callsfindall.library.member.persistence;

import java.util.List;

/** Stands for a framework interface such as {@code CrudRepository<T, K>}: generic, so it says {@code List<T>}. */
public interface CrudLike<T> {
    List<T> findAll();
}
