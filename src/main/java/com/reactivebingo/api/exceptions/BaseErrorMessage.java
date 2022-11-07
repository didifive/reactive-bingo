package com.reactivebingo.api.exceptions;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

@RequiredArgsConstructor
public class BaseErrorMessage {
    private final String DEFAULT_RESOURCE = "messages";

    public static final BaseErrorMessage GENERIC_EXCEPTION = new BaseErrorMessage("generic");
    public static final BaseErrorMessage GENERIC_NOT_FOUND = new BaseErrorMessage("generic.notFound");
    public static final BaseErrorMessage GENERIC_METHOD_NOT_ALLOW = new BaseErrorMessage("generic.methodNotAllow");
    public static final BaseErrorMessage GENERIC_BAD_REQUEST = new BaseErrorMessage("generic.badRequest");
    public static final BaseErrorMessage PLAYER_NOT_FOUND = new BaseErrorMessage("player.notFound");
    public static final BaseErrorMessage EMAIL_ALREADY_USED = new BaseErrorMessage("player.emailAlreadyUsed");

    public static final BaseErrorMessage ROUND_NOT_FOUND = new BaseErrorMessage("round.NotFound");

//    public static final BaseErrorMessage DECK_NOT_FOUND = new BaseErrorMessage("deck.NotFound");
//    public static final BaseErrorMessage STUDY_DECK_NOT_FOUND = new BaseErrorMessage("studyDeck.NotFound");
//    public static final BaseErrorMessage PLAYER_IN_ROUND = new BaseErrorMessage("round.PlayerInRound");
//    public static final BaseErrorMessage STUDY_QUESTION_NOT_FOUND = new BaseErrorMessage("studyQuestion.NotFound");
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
