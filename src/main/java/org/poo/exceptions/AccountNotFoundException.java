package org.poo.exceptions;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends RuntimeException {
    private final String message;

    public AccountNotFoundException(final String message) {
        super(message);
        this.message = message;
    }
}
