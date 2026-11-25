package org.motherheart.examples.jakartadata.shop.common;

import jakarta.data.exceptions.DataException;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.sql.SQLException;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

/** Turns domain exceptions, and refusals of the database itself, into HTTP answers. */
public class ProblemMapper {

    private static final String INTEGRITY_CONSTRAINT_VIOLATION = "23";

    @ServerExceptionMapper
    public Response notFound(DomainNotFoundException exception) {
        return problem(Status.NOT_FOUND, exception.getMessage());
    }

    @ServerExceptionMapper
    public Response rejected(DomainRejectedException exception) {
        return problem(Status.BAD_REQUEST, exception.getMessage());
    }

    /** The heart protects itself: a foreign key or unique constraint refused the change. */
    @ServerExceptionMapper
    public Response refusedByTheDatabase(DataException exception) {
        return databaseProblem(exception);
    }

    @ServerExceptionMapper
    public Response refusedByTheDatabase(PersistenceException exception) {
        return databaseProblem(exception);
    }

    private static Response databaseProblem(RuntimeException exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException sql && sql.getSQLState() != null
                    && sql.getSQLState().startsWith(INTEGRITY_CONSTRAINT_VIOLATION)) {
                return problem(Status.CONFLICT, "refused by the database: " + sql.getMessage().lines().findFirst().orElse(""));
            }
        }
        return problem(Status.INTERNAL_SERVER_ERROR, "persistence failure");
    }

    private static Response problem(Status status, String message) {
        return Response.status(status).entity(new Problem(message)).build();
    }
}
