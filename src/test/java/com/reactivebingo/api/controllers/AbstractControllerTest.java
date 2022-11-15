package com.reactivebingo.api.controllers;

import com.github.javafaker.Faker;
import com.reactivebingo.api.ReactiveBingoApiApplication;
import com.reactivebingo.api.configs.mongo.provider.OffsetDateTimeProvider;
import com.reactivebingo.api.exceptions.handlers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;

@ActiveProfiles("test")
@ContextConfiguration(classes = {OffsetDateTimeProvider.class, ApiExceptionHandler.class,
        CardsLimitReachedHandler.class, PlayerInRoundHandler.class, RoundCompletedHandler.class
        , RoundHasNoDrawnNumberHandler.class, RoundStartedHandler.class
        , EmailAlreadyUsedHandler.class, MethodNotAllowHandler.class, NotFoundHandler.class
        , ConstraintViolationHandler.class, WebExchangeBindHandler.class, ResponseStatusHandler.class
        , ReactiveBingoHandler.class, GenericHandler.class, JsonProcessingHandler.class
        , ReactiveBingoApiApplication.class
})
public abstract class AbstractControllerTest {

    protected final static Faker faker = getFaker();
    @MockBean
    protected MappingMongoConverter mappingMongoConverter;
    @Autowired
    protected ApplicationContext applicationContext;

}

