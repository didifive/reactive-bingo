package com.reactivebingo.api.exceptions.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactivebingo.api.exceptions.ReactiveBingoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.reactivebingo.api.exceptions.BaseErrorMessage.GENERIC_EXCEPTION;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Component
public class ReactiveBingoHandler extends AbstractHandleException<ReactiveBingoException> {

    public ReactiveBingoHandler(final ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public Mono<Void> handlerException(final ServerWebExchange exchange, final ReactiveBingoException ex) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, INTERNAL_SERVER_ERROR);
                    return GENERIC_EXCEPTION.getMessage();
                }).map(message -> buildError(INTERNAL_SERVER_ERROR, message))
                .doFirst(() -> log.error("==== ReactiveBingoException ", ex))
                .flatMap(response -> writeResponse(exchange, response));
    }
}
