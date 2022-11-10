package com.reactivebingo.api.exceptions;

public class CardsLimitReachedException extends ReactiveBingoException {

    public CardsLimitReachedException(final String message) {
        super(message);
    }

}
