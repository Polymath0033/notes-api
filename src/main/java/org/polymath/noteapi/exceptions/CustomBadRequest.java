package org.polymath.noteapi.exceptions;

public class CustomBadRequest extends RuntimeException {
    public CustomBadRequest(String message) {
        super(message);
    }

    public static class UserAlreadyExistsException extends RuntimeException{
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }
}
