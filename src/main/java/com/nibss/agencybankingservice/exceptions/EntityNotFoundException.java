package com.nibss.agencybankingservice.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException() {
        super();
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(Throwable t) {
        super(t);
    }

    public EntityNotFoundException(String msg, Throwable t) {
        super(msg,t);
    }
}
