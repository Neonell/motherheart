package ch.motherheart.examples.jooq.shop.common;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.jooq.exception.DataAccessException;

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
    public Response refusedByTheDatabase(DataAccessException exception) {
        if (exception.sqlState().startsWith(INTEGRITY_CONSTRAINT_VIOLATION)) {
            String detail = exception.getCause() == null ? "" : exception.getCause().getMessage().lines().findFirst().orElse("");
            return problem(Status.CONFLICT, "refused by the database: " + detail);
        }
        return problem(Status.INTERNAL_SERVER_ERROR, "persistence failure");
    }

    private static Response problem(Status status, String message) {
        return Response.status(status).entity(new Problem(message)).build();
    }
}
