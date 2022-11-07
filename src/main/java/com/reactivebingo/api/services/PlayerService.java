package com.reactivebingo.api.services;

import com.reactivebingo.api.documents.PlayerDocument;
import com.reactivebingo.api.documents.PlayerPage;
import com.reactivebingo.api.dtos.PlayerPageRequestDTO;
import com.reactivebingo.api.exceptions.EmailAlreadyUsedException;
import com.reactivebingo.api.exceptions.NotFoundException;
import com.reactivebingo.api.repositories.PlayerRepository;
import com.reactivebingo.api.repositories.PlayerRepositoryImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.reactivebingo.api.exceptions.BaseErrorMessage.EMAIL_ALREADY_USED;
import static com.reactivebingo.api.exceptions.BaseErrorMessage.PLAYER_NOT_FOUND;

@Service
@Slf4j
@AllArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerRepositoryImpl playerRepositoryImpl;

    public Mono<PlayerDocument> findById(final String id) {
        return playerRepository.findById(id)
                .doFirst(() -> log.info("==== try to find player with id {}", id))
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(PLAYER_NOT_FOUND.params("id", id).getMessage()))));
    }

    public Mono<PlayerDocument> findByEmail(final String email) {
        return playerRepository.findByEmail(email)
                .doFirst(() -> log.info("==== try to find player with email {}", email))
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(PLAYER_NOT_FOUND.params("email", email).getMessage()))));
    }

    public Mono<PlayerPage> findOnDemand(final PlayerPageRequestDTO request) {
        return playerRepositoryImpl.findOnDemand(request)
                .collectList()
                .zipWhen(documents -> playerRepositoryImpl.count(request))
                .map(tuple -> PlayerPage.builder()
                        .limit(request.limit())
                        .currentPage(request.page())
                        .totalItems(tuple.getT2())
                        .content(tuple.getT1())
                        .build());
    }

    public Mono<PlayerDocument> save(final PlayerDocument document) {
        return findByEmail(document.email())
                .doFirst(() -> log.info("==== Try to save a follow player {}", document))
                .filter(Objects::isNull)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new EmailAlreadyUsedException(EMAIL_ALREADY_USED
                        .params(document.email()).getMessage()))))
                .onErrorResume(NotFoundException.class, e -> playerRepository.save(document));
    }

    private Mono<Void> verifyEmail(final PlayerDocument document) {
        return findByEmail(document.email())
                .filter(stored -> stored.id().equals(document.id()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new EmailAlreadyUsedException(EMAIL_ALREADY_USED
                        .params(document.email()).getMessage()))))
                .onErrorResume(NotFoundException.class, e -> Mono.empty())
                .then();
    }

    public Mono<PlayerDocument> update(final PlayerDocument document) {
        return verifyEmail(document)
                .then(Mono.defer(() -> findById(document.id())
                        .map(user -> document.toBuilder()
                                .createdAt(user.createdAt())
                                .updatedAt(user.updatedAt())
                                .build())
                        .flatMap(playerRepository::save)
                        .doFirst(() -> log.info("==== Try to update a player with follow info {}", document))));
    }

    public Mono<Void> delete(final String id) {
        return findById(id)
                .flatMap(playerRepository::delete)
                .doFirst(() -> log.info("==== Try to delete a player with follow id {}", id));
    }

}
