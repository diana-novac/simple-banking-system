package org.poo.exceptions;

import lombok.Getter;

@Getter
public class CardNotFoundException extends RuntimeException {
    private final String message;

    public CardNotFoundException(final String message) {
        super(message);
        this.message = message;
    }
}
