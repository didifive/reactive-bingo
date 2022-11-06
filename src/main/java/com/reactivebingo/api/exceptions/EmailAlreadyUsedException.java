package com.reactivebingo.api.exceptions;

public class EmailAlreadyUsedException extends ReactiveBingoException {

    public EmailAlreadyUsedException(final String message) {
        super(message);
    }

}
