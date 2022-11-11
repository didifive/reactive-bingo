package com.reactivebingo.api.exceptions.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactivebingo.api.exceptions.EmailAlreadyUsedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Component
public class EmailAlreadyUsedHandler extends AbstractHandle<EmailAlreadyUsedException> {

    public EmailAlreadyUsedHandler(final ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    Mono<Void> handlerException(final ServerWebExchange exchange, final EmailAlreadyUsedException ex) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, BAD_REQUEST);
                    return ex.getMessage();
                }).map(message -> buildError(BAD_REQUEST, message))
                .doFirst(() -> log.error("==== EmailAlreadyUsedException", ex))
                .flatMap(response -> writeResponse(exchange, response));
    }
}
