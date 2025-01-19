package org.poo.exceptions;

import lombok.Getter;

@Getter
public class RoleException extends RuntimeException {
    private final String message;

    public RoleException(final String message) {
        super(message);
        this.message = message;
    }
}
