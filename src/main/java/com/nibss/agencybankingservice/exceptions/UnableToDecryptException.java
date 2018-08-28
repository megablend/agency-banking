package com.nibss.agencybankingservice.exceptions;

public class UnableToDecryptException extends RuntimeException {

    public UnableToDecryptException() {
        super("Your request could not be decrypted");
    }

    public UnableToDecryptException(String message)  {
        super(message);
    }

    public UnableToDecryptException(String message, Throwable t) {
        super(message, t);
    }

    public  UnableToDecryptException(Throwable t) {
        super(t);
    }
}
