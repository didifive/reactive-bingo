package com.reactivebingo.api.services.queries;

import com.reactivebingo.api.documents.Card;
import com.reactivebingo.api.documents.DrawnNumber;
import com.reactivebingo.api.documents.Page;
import com.reactivebingo.api.documents.RoundDocument;
import com.reactivebingo.api.dtos.requests.RoundPageRequestDTO;
import com.reactivebingo.api.exceptions.NotFoundException;
import com.reactivebingo.api.exceptions.RoundCompletedException;
import com.reactivebingo.api.exceptions.RoundHasNoDrawnNumberException;
import com.reactivebingo.api.repositories.RoundRepository;
import com.reactivebingo.api.repositories.RoundRepositoryImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Objects;

import static com.reactivebingo.api.exceptions.BaseErrorMessage.*;

@Service
@Slf4j
@AllArgsConstructor
public class RoundQueryService {

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

    public Mono<DrawnNumber> getLastDrawnNumber(String id) {
        return roundRepository.findById(id)
                .flatMap(document -> Mono.defer(() -> Mono.just(document.drawnNumbers().stream()
                        .max(Comparator.comparing(DrawnNumber::drawnAt))
                        .orElseThrow(() -> new RoundHasNoDrawnNumberException(ROUND_HAS_NO_DRAWN_NUMBER
                                .params(id).getMessage()))))
                );
    }

    public Mono<Card> findCardByPlayerId(String id, String playerId) {
        return roundRepository.findById(id)
                .doFirst(() ->
                        log.info("==== try to get card in Round with follow id {}" +
                                        " to Player with follow id {}"
                                , id, playerId))
                .flatMap(document -> Flux.fromIterable(document.cards())
                        .filter(c -> c.playerId().equals(playerId))
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(PLAYER_NOT_FOUND.params("id", playerId).getMessage()))))
                        .single());
    }

    public Mono<RoundDocument> checkRoundIncomplete(RoundDocument document) {
        return Flux.fromIterable(document.cards())
                .doFirst(() ->
                        log.info("==== verify if Round with follow id {} was completed", document.id()))
                .filter(card -> card.numbers().size() == card.checkedNumbers().size())
                .count()
                .filter(count -> count <= 0)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RoundCompletedException(ROUND_COMPLETED
                        .params(document.id()).getMessage()))))
                .thenReturn(document);
    }

}
