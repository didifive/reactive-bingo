package com.reactivebingo.api.exceptions;

public class ReactiveBingoException extends RuntimeException{

    public ReactiveBingoException(final String message) {
        super(message);
    }

    public ReactiveBingoException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
