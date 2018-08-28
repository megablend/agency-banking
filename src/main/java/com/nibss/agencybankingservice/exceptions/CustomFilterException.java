package com.nibss.agencybankingservice.exceptions;

public class CustomFilterException extends RuntimeException {

    private String code;
    private int httpStatus;

    public CustomFilterException(String message, String code, int httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
