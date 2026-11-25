package org.motherheart.examples.jakartadata.shop.common;

/** The requested row does not exist. Mapped to HTTP 404. */
public class DomainNotFoundException extends RuntimeException {

    public DomainNotFoundException(String entity, Object id) {
        super(entity + " " + id + " does not exist");
    }
}
