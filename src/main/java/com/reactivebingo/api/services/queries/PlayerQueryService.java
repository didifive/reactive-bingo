package com.reactivebingo.api.services.queries;

import com.reactivebingo.api.documents.Page;
import com.reactivebingo.api.documents.PlayerDocument;
import com.reactivebingo.api.dtos.requests.PlayerPageRequestDTO;
import com.reactivebingo.api.exceptions.NotFoundException;
import com.reactivebingo.api.repositories.PlayerRepository;
import com.reactivebingo.api.repositories.PlayerRepositoryImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.reactivebingo.api.exceptions.BaseErrorMessage.PLAYER_NOT_FOUND;

@Service
@Slf4j
@AllArgsConstructor
public class PlayerQueryService {

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

    public Mono<Page> findOnDemand(final PlayerPageRequestDTO request) {
        return playerRepositoryImpl.findOnDemand(request)
                .collectList()
                .zipWhen(documents -> playerRepositoryImpl.count(request))
                .map(tuple -> Page.builder()
                        .limit(request.limit())
                        .currentPage(request.page())
                        .totalItems(tuple.getT2())
                        .content(tuple.getT1())
                        .build());
    }
}
