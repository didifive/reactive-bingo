package com.reactivebingo.api.utils;

import com.reactivebingo.api.configs.RetryConfig;
import com.reactivebingo.api.exceptions.RetryException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.util.retry.Retry;

import java.util.function.Predicate;

import static com.reactivebingo.api.exceptions.BaseErrorMessage.GENERIC_MAX_RETRIES;

@AllArgsConstructor
@Component
@Slf4j
public class RetryHelper {

    private final RetryConfig retryConfig;

    public Retry processRetry(final String retryIdentifier, final Predicate<? super Throwable> errorFilter) {
        return Retry.backoff(retryConfig.maxRetries(), retryConfig.minDurationSeconds())
                .filter(errorFilter)
                .doBeforeRetry(retrySignal -> log.warn("==== Retrying {} - {} times(s)", retryIdentifier,
                        retrySignal.totalRetries()))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        new RetryException(GENERIC_MAX_RETRIES.getMessage(), retrySignal.failure()));
    }

}
