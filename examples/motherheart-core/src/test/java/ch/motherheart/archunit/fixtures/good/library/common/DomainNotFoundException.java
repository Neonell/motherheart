package ch.motherheart.archunit.fixtures.good.library.common;

public class DomainNotFoundException extends RuntimeException {
    public DomainNotFoundException(String message) {
        super(message);
    }
}
