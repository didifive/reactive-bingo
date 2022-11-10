package com.reactivebingo.api.exceptions.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactivebingo.api.exceptions.RoundHasNoDrawnNumberException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.CONFLICT;

@Component
@Slf4j
public class RoundHasNoDrawnNumberHandler extends AbstractHandleException<RoundHasNoDrawnNumberException> {

    public RoundHasNoDrawnNumberHandler(final ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    Mono<Void> handlerException(final ServerWebExchange exchange, final RoundHasNoDrawnNumberException ex) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, CONFLICT);
                    return ex.getMessage();
                }).map(message -> buildError(CONFLICT, message))
                .doFirst(() -> log.error("==== RoundHasNoDrawnNumberException ", ex))
                .flatMap(response -> writeResponse(exchange, response));
    }
}
