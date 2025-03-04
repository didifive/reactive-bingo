package com.reactivebingo.api.exceptions;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

@RequiredArgsConstructor
public class BaseErrorMessage {
    public static final BaseErrorMessage GENERIC_EXCEPTION = new BaseErrorMessage("generic");
    public static final BaseErrorMessage GENERIC_NOT_FOUND = new BaseErrorMessage("generic.notFound");
    public static final BaseErrorMessage GENERIC_METHOD_NOT_ALLOW = new BaseErrorMessage("generic.methodNotAllow");
    public static final BaseErrorMessage GENERIC_BAD_REQUEST = new BaseErrorMessage("generic.badRequest");
    public static final BaseErrorMessage GENERIC_MAX_RETRIES = new BaseErrorMessage("generic.maxRetries");
    public static final BaseErrorMessage PLAYER_NOT_FOUND = new BaseErrorMessage("player.notFound");
    public static final BaseErrorMessage EMAIL_ALREADY_USED = new BaseErrorMessage("player.emailAlreadyUsed");
    public static final BaseErrorMessage ROUND_NOT_FOUND = new BaseErrorMessage("round.NotFound");
    public static final BaseErrorMessage PLAYER_IN_ROUND = new BaseErrorMessage("round.PlayerInRound");
    public static final BaseErrorMessage ROUND_STARTED = new BaseErrorMessage("round.RoundStarted");
    public static final BaseErrorMessage CARDS_LIMIT_REACHED = new BaseErrorMessage("round.CardsLimitReached");
    public static final BaseErrorMessage ROUND_COMPLETED = new BaseErrorMessage("round.RoundCompleted");
    public static final BaseErrorMessage ROUND_HAS_NO_DRAWN_NUMBER = new BaseErrorMessage("round.RoundHasNoDrawnNumber");

    private static final String DEFAULT_RESOURCE = "messages";
    private final String key;
    private String[] params;

    public BaseErrorMessage params(final String... params) {
        this.params = ArrayUtils.clone(params);
        return this;
    }

    public String getMessage() {
        var message = tryGetMessageFromBundle();
        if (ArrayUtils.isNotEmpty(params)) {
            final var fmt = new MessageFormat(message);
            message = fmt.format(params);
        }
        return message;
    }

    private String tryGetMessageFromBundle() {
        return getResource().getString(key);
    }

    public ResourceBundle getResource() {
        return ResourceBundle.getBundle(DEFAULT_RESOURCE);
    }

}
