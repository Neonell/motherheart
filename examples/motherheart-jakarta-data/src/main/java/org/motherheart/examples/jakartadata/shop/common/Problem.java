package org.motherheart.examples.jakartadata.shop.common;

/** Error body returned by every endpoint. */
public record Problem(String error) {
}
