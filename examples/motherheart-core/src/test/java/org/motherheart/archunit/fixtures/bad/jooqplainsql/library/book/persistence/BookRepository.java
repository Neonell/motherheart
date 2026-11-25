package org.motherheart.archunit.fixtures.bad.jooqplainsql.library.book.persistence;

import org.jooq.DSLContext;
import org.jooq.Result;

public class BookRepository {
    DSLContext dsl;

    Result<?> list() {
        return dsl.fetch("select * from book");
    }
}
