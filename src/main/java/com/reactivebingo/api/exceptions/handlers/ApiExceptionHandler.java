package com.reactivebingo.api.exceptions.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reactivebingo.api.exceptions.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;

@Component
@Order(-2)
@Slf4j
@AllArgsConstructor
public class ApiExceptionHandler implements WebExceptionHandler {

    private final PlayerInRoundHandler playerInRoundHandler;
    private final CardsLimitReachedHandler cardsLimitReachedHandler;
    final RoundCompletedHandler roundCompletedHandler;
    final RoundStartedHandler roundStartedHandler;
    private final EmailAlreadyUsedHandler emailAlreadyUsedHandler;
    private final MethodNotAllowHandler methodNotAllowHandler;
    private final NotFoundHandler notFoundHandler;
    private final ConstraintViolationHandler constraintViolationHandler;
    private final WebExchangeBindHandler webExchangeBindHandler;
    private final ResponseStatusHandler responseStatusHandler;
    private final ReactiveBingoHandler reactiveBingoHandler;
    private final GenericHandler genericHandler;
    private final JsonProcessingHandler jsonProcessingHandler;

    @Override
    public Mono<Void> handle(final ServerWebExchange exchange, final Throwable ex) {
        return Mono.error(ex)
                .onErrorResume(PlayerInRoundException.class, e -> playerInRoundHandler.handlerException(exchange, e))
                .onErrorResume(CardsLimitReachedException.class, e -> cardsLimitReachedHandler.handlerException(exchange, e))
                .onErrorResume(RoundCompletedException.class, e -> roundCompletedHandler.handlerException(exchange, e))
                .onErrorResume(RoundStartedException.class, e -> roundStartedHandler.handlerException(exchange, e))
                .onErrorResume(EmailAlreadyUsedException.class, e -> emailAlreadyUsedHandler.handlerException(exchange, e))
                .onErrorResume(MethodNotAllowedException.class, e -> methodNotAllowHandler.handlerException(exchange, e))
                .onErrorResume(NotFoundException.class, e -> notFoundHandler.handlerException(exchange, e))
                .onErrorResume(ConstraintViolationException.class, e -> constraintViolationHandler.handlerException(exchange, e))
                .onErrorResume(WebExchangeBindException.class, e -> webExchangeBindHandler.handlerException(exchange, e))
                .onErrorResume(ResponseStatusException.class, e -> responseStatusHandler.handlerException(exchange, e))
                .onErrorResume(ReactiveBingoException.class, e -> reactiveBingoHandler.handlerException(exchange, e))
                .onErrorResume(Exception.class, e -> genericHandler.handlerException(exchange, e))
                .onErrorResume(JsonProcessingException.class, e -> jsonProcessingHandler.handlerException(exchange, e))
                .then();
    }

}
