package org.polymath.noteapi.exceptions;

public class UserDoesNotExist extends RuntimeException {
    public UserDoesNotExist(String message) {
        super(message);
    }
}
