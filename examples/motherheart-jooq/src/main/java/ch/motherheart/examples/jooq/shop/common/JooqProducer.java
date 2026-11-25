package ch.motherheart.examples.jooq.shop.common;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

/**
 * No extension, no magic: one explicit DSLContext on top of the Quarkus datasource.
 * The datasource is JTA-aware, so {@code @Transactional} services just work.
 */
public class JooqProducer {

    @Produces
    @Singleton
    DSLContext dslContext(AgroalDataSource dataSource) {
        return DSL.using(dataSource, SQLDialect.POSTGRES, new Settings().withExecuteLogging(true));
    }
}
