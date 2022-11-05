package com.reactivebingo.api.services;

import com.reactivebingo.api.documents.PlayerDocument;
import com.reactivebingo.api.repositories.PlayerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@AllArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public Mono<PlayerDocument> save(final PlayerDocument document){
        return playerRepository.save(document)
                .doFirst(() -> log.info("==== Try to save a follow player {}", document));
    }

}
