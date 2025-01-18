package org.poo.exceptions;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final String message;

    public UserNotFoundException(final String message) {
        super(message);
        this.message = message;
    }
}
