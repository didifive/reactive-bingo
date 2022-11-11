package com.reactivebingo.api.exceptions;

public class RetryException extends ReactiveBingoException{

    public RetryException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
