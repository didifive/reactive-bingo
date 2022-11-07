package com.reactivebingo.api.controllers;

import com.reactivebingo.api.configs.mongo.validation.MongoId;
import com.reactivebingo.api.controllers.docs.RoundControllerDocs;
import com.reactivebingo.api.dtos.*;
import com.reactivebingo.api.dtos.mappers.RoundMapper;
import com.reactivebingo.api.services.RoundService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@RestController
@RequestMapping("rounds")
@Slf4j
@AllArgsConstructor
public class RoundController implements RoundControllerDocs {

    public final RoundService roundService;
    public final RoundMapper roundMapper;

    @Override
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Mono<RoundResponseDTO> save(@Valid @RequestBody final RoundRequestDTO request) {
        return roundService.save(roundMapper.toDocument(request))
                .doFirst(() -> log.info("==== Saving a round with follow data {}", request))
                .map(roundMapper::toResponse);
    }

    @Override
    @PostMapping(produces = APPLICATION_JSON_VALUE, value="{id}/draw-number")
    @ResponseStatus(CREATED)
    public Mono<DrawnNumberDTO> drawNumber(@PathVariable @Valid @MongoId(message = "{roundController.id}") final String id) {
        return null;
//        return roundService.drawNumber(id)
//                .doFirst(() -> log.info("==== Draw a number for a round with follow id {}", id))
//                .map(roundMapper::toResponse);
    }

    @Override
    @GetMapping(produces = APPLICATION_JSON_VALUE, value="{id}/last-number")
    public Mono<DrawnNumberDTO> getLastNumber(@PathVariable @Valid @MongoId(message = "{roundController.id}") final String id) {
        return null;
//        return roundService.getLastNumber(id)
//                .doFirst(() -> log.info("==== Finding last drawn number for a round with follow id {}", id))
//                .map(roundMapper::toResponse);
    }

    @Override
    @PostMapping(produces = APPLICATION_JSON_VALUE, value="{id}/cards/generate/{playerId}")
    @ResponseStatus(CREATED)
    public Mono<CardDTO> generateCard(@PathVariable @Valid @MongoId(message = "{roundController.id}") final String id
    , @PathVariable @Valid @MongoId(message = "{playerController.id}") final String playerId) {
        return null;
//        return roundService.generateCard(id, playerId)
//                .doFirst(() -> log.info("==== Generate a card in a round with follow id {} for a player with follow id {}", id, playerId))
//                .map(roundMapper::toResponse);
    }

    @Override
    @GetMapping(produces = APPLICATION_JSON_VALUE, value="{id}/cards/{cardId}")
    public Mono<CardDTO> findCardById(@PathVariable @Valid @MongoId(message = "{roundController.id}") final String id
            , @PathVariable @Valid @MongoId(message = "{roundController.cardId}") final String cardId) {
        return null;
//        return roundService.findCardById(id, cardId)
//                .doFirst(() -> log.info("==== Findind a card with follow id {} in a round with follow id {}", cardId, id))
//                .map(roundMapper::toResponse);
    }

    @Override
    @GetMapping(produces = APPLICATION_JSON_VALUE, value = "{id}")
    public Mono<RoundResponseDTO> findById(@PathVariable @Valid @MongoId(message = "{roundController.id}") final String id) {
        return roundService.findById(id)
                .doFirst(() -> log.info("==== Finding a round with follow id {}", id))
                .map(roundMapper::toResponse);
    }

    @Override
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Mono<PageResponseDTO> findAll(@Valid final RoundPageRequestDTO request){
        return null;
//        return roundService.findOnDemand(request)
//                .doFirst(() -> log.info("==== Finding rounds on demand with follow request {}", request))
//                .map(page -> roundMapper.toResponse(page, request.limit()));
    }
}
