package com.reactivebingo.api.controllers;

import com.reactivebingo.api.controllers.docs.PlayerControllerDocs;
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
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@RestController
@RequestMapping("players")
@Slf4j
@AllArgsConstructor
public class PlayerController implements PlayerControllerDocs {

    public final PlayerService playerService;
    public final PlayerMapper playerMapper;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Mono<PlayerResponseDTO> save(@Valid @RequestBody final PlayerRequestDTO request) {
        return playerService.save(playerMapper.toDocument(request))
                .doFirst(() -> log.info("==== Saving a player with follow data {}", request))
                .map(playerMapper::toResponse);
    }

    @GetMapping()
    public Mono<String> findAll() {
        return Mono.just("Olá");
    }
}
