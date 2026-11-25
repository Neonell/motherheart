package org.motherheart.examples.jakartadata.shop.common;

/** The request is understood but refused by a domain rule. Mapped to HTTP 400. */
public class DomainRejectedException extends RuntimeException {

    public DomainRejectedException(String message) {
        super(message);
    }
}
