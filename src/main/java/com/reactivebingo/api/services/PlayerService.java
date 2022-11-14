package com.reactivebingo.api.services;

import com.reactivebingo.api.documents.PlayerDocument;
import com.reactivebingo.api.exceptions.EmailAlreadyUsedException;
import com.reactivebingo.api.exceptions.NotFoundException;
import com.reactivebingo.api.repositories.PlayerRepository;
import com.reactivebingo.api.services.queries.PlayerQueryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.reactivebingo.api.exceptions.BaseErrorMessage.EMAIL_ALREADY_USED;

@Service
@Slf4j
@AllArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerQueryService playerQueryService;

    public Mono<PlayerDocument> save(final PlayerDocument document) {
        return playerQueryService.findByEmail(document.email())
                .doFirst(() -> log.info("==== Try to save a follow player {}", document))
                .filter(Objects::isNull)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new EmailAlreadyUsedException(EMAIL_ALREADY_USED
                        .params(document.email()).getMessage()))))
                .onErrorResume(NotFoundException.class, e -> playerRepository.save(document));
    }

    private Mono<Void> verifyEmail(final PlayerDocument document) {
        return playerQueryService.findByEmail(document.email())
                .filter(stored -> stored.id().equals(document.id()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new EmailAlreadyUsedException(EMAIL_ALREADY_USED
                        .params(document.email()).getMessage()))))
                .onErrorResume(NotFoundException.class, e -> Mono.empty())
                .then();
    }

    public Mono<PlayerDocument> update(final PlayerDocument document) {
        return verifyEmail(document)
                .then(Mono.defer(() -> playerQueryService.findById(document.id())
                        .map(user -> document.toBuilder()
                                .createdAt(user.createdAt())
                                .updatedAt(user.updatedAt())
                                .build())
                        .flatMap(playerRepository::save)
                        .doFirst(() -> log.info("==== Try to update a player with follow info {}", document))));
    }

    public Mono<Void> delete(final String id) {
        return playerQueryService.findById(id)
                .flatMap(playerRepository::delete)
                .doFirst(() -> log.info("==== Try to delete a player with follow id {}", id));
    }

}
