package org.poo.exceptions;

import lombok.Getter;

@Getter
public class AccountTypeException extends RuntimeException {
    private final String message;

    public AccountTypeException(final String message) {
        super(message);
        this.message = message;
    }
}
