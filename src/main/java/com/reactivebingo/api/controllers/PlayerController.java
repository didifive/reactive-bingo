package com.reactivebingo.api.controllers;

import com.reactivebingo.api.configs.mongo.validation.MongoId;
import com.reactivebingo.api.controllers.docs.PlayerControllerDocs;
import com.reactivebingo.api.dtos.PageResponseDTO;
import com.reactivebingo.api.dtos.PlayerPageRequestDTO;
import com.reactivebingo.api.dtos.PlayerRequestDTO;
import com.reactivebingo.api.dtos.PlayerResponseDTO;
import com.reactivebingo.api.dtos.mappers.PlayerMapper;
import com.reactivebingo.api.services.PlayerService;
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
@RequestMapping("players")
@Slf4j
@AllArgsConstructor
public class PlayerController implements PlayerControllerDocs {

    public final PlayerService playerService;
    public final PlayerMapper playerMapper;

    @Override
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Mono<PlayerResponseDTO> save(@Valid @RequestBody final PlayerRequestDTO request) {
        return playerService.save(playerMapper.toDocument(request))
                .doFirst(() -> log.info("==== Saving a player with follow data {}", request))
                .map(playerMapper::toResponse);
    }

    @Override
    @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE, value = "{id}")
    public Mono<PlayerResponseDTO> update(@PathVariable @Valid @MongoId(message = "{playerController.id}") final String id
            , @Valid @RequestBody final PlayerRequestDTO request) {
        return playerService.update(playerMapper.toDocument(request, id))
                .doFirst(() -> log.info("==== Updating a player with follow info [body: {}, id: {}]", request, id))
                .map(playerMapper::toResponse);
    }

    @Override
    @DeleteMapping(value = "{id}")
    @ResponseStatus(NO_CONTENT)
    public Mono<Void> delete(@PathVariable @Valid @MongoId(message = "{playerController.id}") final String id) {
        return playerService.delete(id)
                .doFirst(() -> log.info("==== Deleting a player with follow id {}", id));
    }

    @Override
    @GetMapping(produces = APPLICATION_JSON_VALUE, value = "{id}")
    public Mono<PlayerResponseDTO> findBy(@PathVariable @Valid @MongoId(message = "{playerController.id}") final String id) {
        return playerService.findById(id)
                .doFirst(() -> log.info("==== Finding a player with follow id {}", id))
                .map(playerMapper::toResponse);
    }

    @Override
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Mono<PageResponseDTO> findAll(@Valid final PlayerPageRequestDTO request) {
        return playerService.findOnDemand(request)
                .doFirst(() -> log.info("==== Finding players on demand with follow request {}", request))
                .map(page -> playerMapper.toResponse(page, request.limit()));
    }
}
