package org.paul.participantservice.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException(String message) {
        super(message);
    }
}
