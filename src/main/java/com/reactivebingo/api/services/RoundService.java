package com.reactivebingo.api.services;

import com.reactivebingo.api.documents.Card;
import com.reactivebingo.api.documents.DrawnNumber;
import com.reactivebingo.api.documents.RoundDocument;
import com.reactivebingo.api.dtos.mappers.MailMapper;
import com.reactivebingo.api.exceptions.*;
import com.reactivebingo.api.mappers.CardDomainMapper;
import com.reactivebingo.api.mappers.DrawnNumberDomainMapper;
import com.reactivebingo.api.repositories.RoundRepository;
import com.reactivebingo.api.services.queries.PlayerQueryService;
import com.reactivebingo.api.services.queries.RoundQueryService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static com.reactivebingo.api.exceptions.BaseErrorMessage.*;
import static java.time.ZoneOffset.UTC;

@Service
@Slf4j
@AllArgsConstructor
public class RoundService {

    public static final Integer MAX_DECKS_PER_ROUND = 120;
    private final RoundRepository roundRepository;
    private final RoundQueryService roundQueryService;
    private final CardDomainMapper cardDomainMapper;
    private final DrawnNumberDomainMapper drawnNumberDomainMapper;
    private final MailMapper mailMapper;
    private final PlayerQueryService playerQueryService;
    private final MailService mailService;

    public Mono<RoundDocument> save(final RoundDocument document) {
        return roundRepository.save(document)
                .doFirst(() -> log.info("==== Try to save a follow round {}", document));
    }

    public Mono<Card> generateCard(final String id, final String playerId) {
        return roundQueryService.findById(id)
                .doFirst(() ->
                        log.info("==== try to generate card in round with follow id {}" +
                                        " to player with follow id {}"
                                , id, playerId))
                .flatMap(document -> verifyPlayer(document, playerId))
                .flatMap(this::verifyCardsLimitReached)
                .flatMap(this::verifyRoundNotStarted)
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
                .zipWhen(doc -> playerQueryService.findById(playerId))
                .map(Tuple2::getT1)
                .filter(d -> d.cards().stream().noneMatch(card -> card.playerId().equals(playerId)))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new PlayerInRoundException(PLAYER_IN_ROUND
                        .params(playerId, document.id()).getMessage()))));
    }

    private Mono<RoundDocument> verifyCardsLimitReached(RoundDocument document) {
        return Mono.just(document)
                .doFirst(() ->
                        log.info("==== verify if a round with a follow id {} reached the limit of cards"
                                , document.id()))
                .filter(d -> d.cards().size() < MAX_DECKS_PER_ROUND)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CardsLimitReachedException(CARDS_LIMIT_REACHED
                        .params(document.id()).getMessage()))));
    }

    private Mono<RoundDocument> verifyRoundNotStarted(RoundDocument document) {
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
                        .map(this::checkLimitRepeat)
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

    protected Mono<Set<Short>> numbersToCard(Card card) {
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

    public Mono<DrawnNumber> drawNumber(String id) {
        return roundQueryService.findById(id)
                .doFirst(() ->
                        log.info("==== try to drawn number to Round with follow id {}", id))
                .flatMap(roundQueryService::checkRoundIncomplete)
                .zipWhen(this::drawNumberToRound)
                .map(tuple -> drawnNumberDomainMapper.addDrawnNumberToRound(tuple.getT1(), tuple.getT2()))
                .flatMap(this::updateCards)
                .flatMap(this::save)
                .flatMap(this::checkRoundNowCompletedToNotify)
                .flatMap(document -> Mono.defer(() -> Mono.just(document.drawnNumbers().stream()
                        .max(Comparator.comparing(DrawnNumber::drawnAt))
                        .orElseThrow())));
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

    private Mono<RoundDocument> updateCards(RoundDocument document) {
        return Flux.fromIterable(document.cards())
                .map(card -> cardDomainMapper.toCardWithCheckedNumbers(card, document.drawnNumbers()))
                .collect(Collectors.toSet())
                .thenReturn(document);
    }

    private Mono<RoundDocument> checkRoundNowCompletedToNotify(RoundDocument roundDocument) {
        return Mono.just(roundDocument)
                .flatMap(roundQueryService::checkRoundIncomplete)
                .onErrorResume(RoundCompletedException.class, e -> Mono.just(roundDocument)
                        .onTerminateDetach()
                        .doOnSuccess(this::notifyPlayers));
    }

    private void notifyPlayers(final RoundDocument document) {
        Flux.fromIterable(document.cards())
                .doFirst(() ->
                        log.info("==== try to notify Players"))
                .flatMap(card ->
                        Mono.just(card)
                                .zipWhen(c -> playerQueryService.findById(c.playerId())))
                .map(tuple -> mailMapper.toDTO(document, tuple.getT1(), tuple.getT2()))
                .flatMap(mailService::send)
                .subscribe();
    }
}
