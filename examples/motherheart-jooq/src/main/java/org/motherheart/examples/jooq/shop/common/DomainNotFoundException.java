package ch.motherheart.examples.jooq.shop.common;

/** The requested row does not exist. Mapped to HTTP 404. */
public class DomainNotFoundException extends RuntimeException {

    public DomainNotFoundException(String what, Object id) {
        super(what + " " + id + " does not exist");
    }
}
