package org.motherheart.examples.jooq.shop.common;

/** Error body returned by every endpoint. */
public record Problem(String error) {
}
