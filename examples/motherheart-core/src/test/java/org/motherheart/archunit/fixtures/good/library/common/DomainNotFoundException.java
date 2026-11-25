package org.motherheart.archunit.fixtures.good.library.common;

public class DomainNotFoundException extends RuntimeException {
    public DomainNotFoundException(String message) {
        super(message);
    }
}
