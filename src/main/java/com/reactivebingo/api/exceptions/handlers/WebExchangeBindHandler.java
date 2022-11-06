package com.reactivebingo.api.exceptions.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactivebingo.api.dtos.ErrorFieldResponseDTO;
import com.reactivebingo.api.dtos.ProblemResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.reactivebingo.api.exceptions.BaseErrorMessage.GENERIC_BAD_REQUEST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Component
public class WebExchangeBindHandler extends AbstractHandleException<WebExchangeBindException> {

    private final MessageSource messageSource;

    public WebExchangeBindHandler(final ObjectMapper mapper, final MessageSource messageSource) {
        super(mapper);
        this.messageSource = messageSource;
    }

    @Override
    public Mono<Void> handlerException(final ServerWebExchange exchange, final WebExchangeBindException ex) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, BAD_REQUEST);
                    return GENERIC_BAD_REQUEST.getMessage();
                }).map(message -> buildError(BAD_REQUEST, message))
                .flatMap(response -> buildRequestErrorMessage(response, ex))
                .doFirst(() -> log.error("==== WebExchangeBindException", ex))
                .flatMap(response -> writeResponse(exchange, response));
    }

    private Mono<ProblemResponseDTO> buildRequestErrorMessage(final ProblemResponseDTO response, final WebExchangeBindException ex) {
        return Flux.fromIterable(ex.getAllErrors())
                .map(objectError -> ErrorFieldResponseDTO.builder()
                        .name(objectError instanceof FieldError fieldError ? fieldError.getField() : objectError.getObjectName())
                        .message(messageSource.getMessage(objectError, LocaleContextHolder.getLocale()))
                        .build())
                .collectList()
                .map(problems -> response.toBuilder().fields(problems).build());
    }

}
