package com.reactivebingo.api.services;

import com.reactivebingo.api.documents.Card;
import com.reactivebingo.api.documents.DrawnNumber;
import com.reactivebingo.api.documents.Page;
import com.reactivebingo.api.documents.RoundDocument;
import com.reactivebingo.api.dtos.requests.RoundPageRequestDTO;
import com.reactivebingo.api.exceptions.*;
import com.reactivebingo.api.mappers.CardDomainMapper;
import com.reactivebingo.api.mappers.DrawnNumberDomainMapper;
import com.reactivebingo.api.repositories.RoundRepository;
import com.reactivebingo.api.repositories.RoundRepositoryImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static com.reactivebingo.api.exceptions.BaseErrorMessage.*;
import static java.time.ZoneOffset.UTC;

@Service
@Slf4j
@AllArgsConstructor
public class RoundService {

    private final static Integer MAX_DECKS_PER_ROUND = 500;
    private final RoundRepository roundRepository;
    private final RoundRepositoryImpl roundRepositoryImpl;
    private final CardDomainMapper cardDomainMapper;
    private final DrawnNumberDomainMapper drawnNumberDomainMapper;
    private final PlayerService playerService;

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

    public Mono<RoundDocument> save(final RoundDocument document) {
        return roundRepository.save(document)
                .doFirst(() -> log.info("==== Try to save a follow round {}", document));
    }

    public Mono<Card> generateCard(final String id, final String playerId) {
        return findById(id)
                .doFirst(() ->
                        log.info("==== try to generate card in round with follow id {}" +
                                        " to player with follow id {}"
                                , id, playerId))
                .flatMap(document -> verifyPlayer(document, playerId))
                .flatMap(this::verifyRoundNotStart)
                .flatMap(this::verifyCardsLimitReached)
                .flatMap(document -> createNewCard(document, playerId))
                .flatMap(this::save)
                .flatMap(document -> Flux.fromIterable(document.cards())
                        .filter(card -> card.playerId().equals(playerId))
                        .single());
    }

    private Mono<RoundDocument> verifyPlayer(RoundDocument document, final String playerId) {
        return Mono.just(document)
                .doFirst(() ->
                        log.info("==== verify if a player with follow id {} exists and if is already in" +
                                        " a round with follow id {}"
                                , playerId, document.id()))
                .zipWhen(doc -> playerService.findById(playerId))
                .map(tuple -> tuple.getT1())
                .filter(d -> d.cards().stream().noneMatch(card -> card.playerId().equals(playerId)))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new PlayerInRoundException(PLAYER_IN_ROUND
                        .params(playerId, document.id()).getMessage()))));
    }

    private Mono<RoundDocument> verifyRoundNotStart(RoundDocument document) {
        return Mono.just(document)
                .doFirst(() ->
                        log.info("==== verify if a round with a follow id {} reached the limit of cards"
                                , document.id()))
                .filter(d -> d.cards().size() < MAX_DECKS_PER_ROUND)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CardsLimitReachedException(CARDS_LIMIT_REACHED
                        .params(document.id()).getMessage()))));
    }

    private Mono<RoundDocument> verifyCardsLimitReached(RoundDocument document) {
        return Mono.just(document)
                .doFirst(() ->
                        log.info("==== verify if a round with follow id {} is not started"
                                , document.id()))
                .filter(d -> d.drawnNumbers().isEmpty())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RoundStartedException(ROUND_STARTED
                        .params(document.id()).getMessage()))));
    }

    private Mono<RoundDocument> createNewCard(RoundDocument document, String playerId) {
        return Mono.just(Card.builder().playerId(playerId).createdAt(OffsetDateTime.now(UTC)).build())
                .doFirst(() ->
                        log.info("==== try to generate card to player with follow id {}"
                                , playerId))
                .zipWhen(this::numbersToCard)
                .map(tuple -> cardDomainMapper.toCardWithNumber(tuple.getT1(), tuple.getT2()))
                .zipWhen(card -> checkCardInRound(document, card))
                .map(tuple -> cardDomainMapper.addCardToDocument(tuple.getT1(), tuple.getT2()))
                .onErrorResume(RepeatedNumbersException.class, err -> createNewCard(document, playerId));
    }


    private Mono<RoundDocument> checkCardInRound(RoundDocument document, Card card) {
        return Flux.fromIterable(document.cards())
                .doFirst(() ->
                        log.info("==== verify rule to generate a card for a round with a follow id {}"
                                , document.id()))
                .flatMap(c -> Flux.fromIterable(c.numbers())
                        .filter(n -> card.numbers().contains(n))
                        .count()
                        .map(v -> checkLimitRepeat(v))
                )
                .count()
                .thenReturn(document);
    }

    private Mono<Void> checkLimitRepeat(Long value) {
        return Mono.just(value)
                .filter(v -> v <= 5)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RepeatedNumbersException())))
                .then();
    }

    private Mono<Set<Short>> numbersToCard(Card card) {
        return Mono.just(card)
                .doFirst(() ->
                        log.info("==== try to generate numbers for a follow card {}"
                                , card))
                .map(c -> new Random()
                        .ints(20, 0, 99)
                        .boxed()
                        .map(Integer::shortValue)
                        .collect(Collectors.toSet()))
                .filter(numbers -> numbers.size() == 20)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new InvalidAmountNumbersException())))
                .onErrorStop()
                .onErrorResume(InvalidAmountNumbersException.class, err -> numbersToCard(card));
    }

    public Mono<Card> findCardByPlayerId(String id, String playerId) {
        return findById(id)
                .doFirst(() ->
                        log.info("==== try to get card in Round with follow id {}" +
                                        " to Player with follow id {}"
                                , id, playerId))
                .flatMap(document -> verifyPlayer(document, playerId))
                .flatMap(document -> Flux.fromIterable(document.cards())
                        .filter(c -> c.playerId().equals(playerId))
                        .single());
    }

    public Mono<DrawnNumber> drawNumber(String id) {
        return findById(id)
                .doFirst(() ->
                        log.info("==== try to drawn number to Round with follow id {}", id))
                .flatMap(this::checkRoundComplete)
                .zipWhen(this::drawNumberToRound)
                .map(tuple -> drawnNumberDomainMapper.addDrawnNumberToRound(tuple.getT1(), tuple.getT2()))
                .flatMap(this::updateCards)
                .flatMap(this::save)
                .flatMap(document -> Mono.defer(() -> Mono.just(document.drawnNumbers().stream()
                        .max(Comparator.comparing(n -> n.drawnAt()))
                        .get())));
    }


    private Mono<DrawnNumber> drawNumberToRound(RoundDocument document) {
        return Mono.just(DrawnNumber.builder()
                        .number(Short.parseShort(String.valueOf(new Random().nextInt(99))))
                        .drawnAt(OffsetDateTime.now(UTC)).build())
                .doFirst(() ->
                        log.info("==== try draw a new number to a Round with follow id {}", document.id()))
                .filter(drawnNumber -> document.drawnNumbers().stream().noneMatch(
                        number -> number.number().equals(drawnNumber.number())))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new InvalidDrawnNumberException())))
                .onErrorStop()
                .onErrorResume(InvalidDrawnNumberException.class, err -> drawNumberToRound(document));
    }

    private Mono<RoundDocument> checkRoundComplete(RoundDocument document) {
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

    private Mono<RoundDocument> updateCards(RoundDocument document) {
        return Flux.fromIterable(document.cards())
                .map(card -> cardDomainMapper.toCardWithCheckedNumbers(card, document.drawnNumbers()))
                .collect(Collectors.toSet())
                .zipWhen(cards -> Mono.just(document))
                .map(tuple -> cardDomainMapper.addCardsToDocument(tuple.getT1(), tuple.getT2()));
    }

    public Mono<DrawnNumber> getLastNumber(String id) {
        return findById(id)
                .flatMap(document -> Mono.defer(() -> Mono.just(document.drawnNumbers().stream()
                        .max(Comparator.comparing(n -> n.drawnAt()))
                        .orElseThrow(() -> new RoundHasNoDrawnNumberException(ROUND_HAS_NO_DRAWN_NUMBER
                                .params(id).getMessage()))))
                );
    }
}
