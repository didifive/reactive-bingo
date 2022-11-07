package com.reactivebingo.api.services;

import com.reactivebingo.api.documents.RoundDocument;
import com.reactivebingo.api.exceptions.NotFoundException;
import com.reactivebingo.api.repositories.RoundRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.reactivebingo.api.exceptions.BaseErrorMessage.ROUND_NOT_FOUND;

@Service
@Slf4j
@AllArgsConstructor
public class RoundService {

    private final RoundRepository roundRepository;

    public Mono<RoundDocument> findById(final String id) {
        return roundRepository.findById(id)
                .doFirst(() -> log.info("==== try to find round with id {}", id))
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(ROUND_NOT_FOUND.params("id", id).getMessage()))));
    }

//    public Mono<RoundDocument> findByEmail(final String email) {
//        return roundRepository.findByEmail(email)
//                .doFirst(() -> log.info("==== try to find player with email {}", email))
//                .filter(Objects::nonNull)
//                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(PLAYER_NOT_FOUND.params("email", email).getMessage()))));
//    }
//
//    public Mono<PlayerPage> findOnDemand(final PlayerPageRequestDTO request) {
//        return playerRepositoryImpl.findOnDemand(request)
//                .collectList()
//                .zipWhen(documents -> playerRepositoryImpl.count(request))
//                .map(tuple -> PlayerPage.builder()
//                        .limit(request.limit())
//                        .currentPage(request.page())
//                        .totalItems(tuple.getT2())
//                        .content(tuple.getT1())
//                        .build());
//    }
//
    public Mono<RoundDocument> save(final RoundDocument document){
        return roundRepository.save(document)
                .doFirst(() -> log.info("==== Try to save a follow round {}", document));
    }
//
//    private Mono<Void> verifyEmail(final RoundDocument document){
//        return findByEmail(document.email())
//                .filter(stored -> stored.id().equals(document.id()))
//                .switchIfEmpty(Mono.defer(() ->Mono.error(new EmailAlreadyUsedException(EMAIL_ALREADY_USED
//                        .params(document.email()).getMessage()))))
//                .onErrorResume(NotFoundException.class, e -> Mono.empty())
//                .then();
//    }
//
//    public Mono<RoundDocument> update(final RoundDocument document){
//        return verifyEmail(document)
//                .then(Mono.defer(() -> findById(document.id())
//                        .map(user -> document.toBuilder()
//                                .createdAt(user.createdAt())
//                                .updatedAt(user.updatedAt())
//                                .build())
//                        .flatMap(roundRepository::save)
//                        .doFirst(() -> log.info("==== Try to update a player with follow info {}", document))));
//    }
//
//    public Mono<Void> delete(final String id){
//        return findById(id)
//                .flatMap(roundRepository::delete)
//                .doFirst(() -> log.info("==== Try to delete a player with follow id {}", id));
//    }

}
