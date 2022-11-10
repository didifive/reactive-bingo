package com.reactivebingo.api.controllers;

import com.reactivebingo.api.configs.mongo.validation.MongoId;
import com.reactivebingo.api.controllers.docs.RoundControllerDocs;
import com.reactivebingo.api.dtos.*;
import com.reactivebingo.api.dtos.mappers.CardMapper;
import com.reactivebingo.api.dtos.mappers.DrawnNumberMapper;
import com.reactivebingo.api.dtos.mappers.RoundMapper;
import com.reactivebingo.api.dtos.requests.RoundPageRequestDTO;
import com.reactivebingo.api.dtos.requests.RoundRequestDTO;
import com.reactivebingo.api.dtos.responses.PageResponseDTO;
import com.reactivebingo.api.dtos.responses.RoundResponseDTO;
import com.reactivebingo.api.services.RoundService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@RestController
@RequestMapping("rounds")
@Slf4j
@AllArgsConstructor
public class RoundController implements RoundControllerDocs {

    public final RoundService roundService;
    public final RoundMapper roundMapper;
    public final CardMapper cardMapper;
    public final DrawnNumberMapper drawnNumberMapper;

    @Override
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Mono<RoundResponseDTO> save(@Valid @RequestBody final RoundRequestDTO request) {
        return roundService.save(roundMapper.toDocument(request))
                .doFirst(() -> log.info("==== Saving a Round with follow data {}", request))
                .map(roundMapper::toPageResponse);
    }

    @Override
    @PostMapping(produces = APPLICATION_JSON_VALUE, value="{id}/draw-number")
    @ResponseStatus(CREATED)
    public Mono<DrawnNumberDTO> drawNumber(@PathVariable @Valid @MongoId(message = "{roundController.id}") final String id) {
        return roundService.drawNumber(id)
                .doFirst(() -> log.info("==== Draw a number for a round with follow id {}", id))
                .map(drawnNumberMapper::toDto);
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
        return roundService.generateCard(id, playerId)
                .doFirst(() -> log.info("==== Generate a card in a Round with follow id {} for a Player with follow id {}", id, playerId))
                .map(cardMapper::toDto);
    }

    @Override
    @GetMapping(produces = APPLICATION_JSON_VALUE, value="{id}/cards/get/{playerId}")
    public Mono<CardDTO> findCardByPlayerId(@PathVariable @Valid @MongoId(message = "{roundController.id}") final String id
            , @PathVariable @Valid @MongoId(message = "{playerController.id}") final String playerId) {
        return roundService.findCardByPlayerId(id, playerId)
                .doFirst(() -> log.info("==== Findind a card of the Player with follow id {} in a Round with follow id {}", playerId, id))
                .map(cardMapper::toDto);
    }

    @Override
    @GetMapping(produces = APPLICATION_JSON_VALUE, value = "{id}")
    public Mono<RoundResponseDTO> findById(@PathVariable @Valid @MongoId(message = "{roundController.id}") final String id) {
        return roundService.findById(id)
                .doFirst(() -> log.info("==== Finding a Round with follow id {}", id))
                .map(roundMapper::toPageResponse);
    }

    @Override
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Mono<PageResponseDTO> findAll(@Valid final RoundPageRequestDTO request){
        return roundService.findOnDemand(request)
                .doFirst(() -> log.info("==== Finding rounds on demand with follow request {}", request))
                .map(page -> roundMapper.toPageResponse(page, request.limit()));
    }
}
