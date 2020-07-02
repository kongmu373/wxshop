package com.kongmu373.wxshop.exception;

public class ErrorException extends RuntimeException {
    private final int code;
    public ErrorException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
