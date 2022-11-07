package com.reactivebingo.api.services;

import com.reactivebingo.api.documents.Page;
import com.reactivebingo.api.documents.RoundDocument;
import com.reactivebingo.api.dtos.RoundPageRequestDTO;
import com.reactivebingo.api.exceptions.NotFoundException;
import com.reactivebingo.api.repositories.RoundRepository;
import com.reactivebingo.api.repositories.RoundRepositoryImpl;
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
    private final RoundRepositoryImpl roundRepositoryImpl;

    public Mono<RoundDocument> findById(final String id) {
        return roundRepository.findById(id)
                .doFirst(() -> log.info("==== try to find round with id {}", id))
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(ROUND_NOT_FOUND.params("id", id).getMessage()))));
    }

    public Mono<Page> findOnDemand(final RoundPageRequestDTO request) {
        return roundRepositoryImpl.findOnDemand(request)
                .collectList()
                .zipWhen(documents -> roundRepositoryImpl.count(request))
                .map(tuple -> Page.builder()
                        .limit(request.limit())
                        .currentPage(request.page())
                        .totalItems(tuple.getT2())
                        .content(tuple.getT1())
                        .build());
    }

    public Mono<RoundDocument> save(final RoundDocument document){
        return roundRepository.save(document)
                .doFirst(() -> log.info("==== Try to save a follow round {}", document));
    }

}
