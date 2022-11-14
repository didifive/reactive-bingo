package com.reactivebingo.api.services.queries;

import com.github.javafaker.Faker;
import com.reactivebingo.api.documents.Card;
import com.reactivebingo.api.documents.DrawnNumber;
import com.reactivebingo.api.documents.RoundDocument;
import com.reactivebingo.api.dtos.requests.RoundPageRequestDTO;
import com.reactivebingo.api.exceptions.NotFoundException;
import com.reactivebingo.api.exceptions.RoundCompletedException;
import com.reactivebingo.api.exceptions.RoundHasNoDrawnNumberException;
import com.reactivebingo.api.repositories.RoundRepository;
import com.reactivebingo.api.repositories.RoundRepositoryImpl;
import com.reactivebingo.api.utils.factorybot.documents.RoundDocumentFactoryBot;
import com.reactivebingo.api.utils.factorybot.dtos.requests.RoundPageRequestFactoryBot;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static com.reactivebingo.api.exceptions.BaseErrorMessage.PLAYER_NOT_FOUND;
import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RoundQueryServiceTest {

    private final static Faker faker = getFaker();
    @Mock
    private RoundRepository roundRepository;
    @Mock
    private RoundRepositoryImpl roundRepositoryImpl;
    private RoundQueryService roundQueryService;

    private static Stream<Arguments> findOnDemandTest() {
        var documents = Stream.generate(() -> RoundDocumentFactoryBot.builder().build())
                .limit(faker.number().randomDigitNotZero())
                .toList();
        var pageRequest = RoundPageRequestFactoryBot.builder().build();
        var total = faker.number().numberBetween(documents.size(), documents.size() * 3L);
        return Stream.of(
                Arguments.of(documents,
                        total,
                        pageRequest,
                        (total / pageRequest.limit()) + ((total % pageRequest.limit() > 0) ? 1 : 0)
                ),
                Arguments.of(new ArrayList<>(), 0L, RoundPageRequestFactoryBot.builder().build(), 0L)
        );
    }

    @BeforeEach
    void setup() {
        roundQueryService = new RoundQueryService(roundRepository, roundRepositoryImpl);
    }

    @Test
    void findByIdTest() {
        var document = RoundDocumentFactoryBot.builder().build();
        when(roundRepository.findById(anyString())).thenReturn(Mono.just(document));

        StepVerifier.create(roundQueryService.findById(ObjectId.get().toString()))
                .assertNext(actual -> assertThat(actual).usingRecursiveComparison()
                        .ignoringFields("createdAt", "updatedAt")
                        .isEqualTo(document))
                .verifyComplete();
        verify(roundRepository).findById(anyString());
        verifyNoInteractions(roundRepositoryImpl);
    }

    @Test
    void whenTryToFindNonStoredRoundByIdThenThrowError() {
        when(roundRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(roundQueryService.findById(ObjectId.get().toString()))
                .verifyError(NotFoundException.class);
        verify(roundRepository).findById(anyString());
        verifyNoInteractions(roundRepositoryImpl);
    }

    @MethodSource
    @ParameterizedTest
    void findOnDemandTest(final List<RoundDocument> documents, final Long total,
                          final RoundPageRequestDTO pageRequest, final Long expectTotalPages) {
        when(roundRepositoryImpl.findOnDemand(any(RoundPageRequestDTO.class))).thenReturn(Flux.fromIterable(documents));
        when(roundRepositoryImpl.count(any(RoundPageRequestDTO.class))).thenReturn(Mono.just(total));

        StepVerifier.create(roundQueryService.findOnDemand(pageRequest))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat((List<RoundDocument>) actual.content()).containsExactlyInAnyOrderElementsOf(documents);
                    assertThat(actual.totalItems()).isEqualTo(total);
                    assertThat(actual.totalPages()).isEqualTo(expectTotalPages);
                })
                .verifyComplete();
    }

    @Test
    void getLastDrawnNumberTest() {
        var round = RoundDocumentFactoryBot.builder().withDrawnNumbers(50).build();
        var lastDrawnNumber = round.drawnNumbers().stream()
                .max(Comparator.comparing(DrawnNumber::drawnAt))
                .map(DrawnNumber::number)
                .orElseThrow();
        when(roundRepository.findById(anyString())).thenReturn(Mono.just(round));

        StepVerifier.create(roundQueryService.getLastDrawnNumber(ObjectId.get().toString()))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual.number()).isEqualTo(lastDrawnNumber);
                })
                .verifyComplete();
        verify(roundRepository).findById(anyString());
    }

    @Test
    void whenTryToGetLastDrawnNumberFromUnstartedRoundThenThrowError() {
        var round = RoundDocumentFactoryBot.builder().build();
        when(roundRepository.findById(anyString())).thenReturn(Mono.just(round));

        StepVerifier.create(roundQueryService.getLastDrawnNumber(ObjectId.get().toString()))
                .verifyError(RoundHasNoDrawnNumberException.class);
        verify(roundRepository).findById(anyString());
    }

    @Test
    void getCardByPlayerIdTest() {
        var playerId = ObjectId.get().toString();
        var round = RoundDocumentFactoryBot.builder().withCardToPlayer(playerId).build();
        when(roundRepository.findById(anyString())).thenReturn(Mono.just(round));

        StepVerifier.create(roundQueryService.findCardByPlayerId(ObjectId.get().toString(), playerId))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual.playerId()).isEqualTo(playerId);
                })
                .verifyComplete();
        verify(roundRepository).findById(anyString());
    }

    @Test
    void whenTryToGetCardByPlayerIsNotInRoundThenThrowError() {
        var invalidPlayerId = ObjectId.get().toString();
        var round = RoundDocumentFactoryBot.builder().build();
        when(roundRepository.findById(anyString())).thenReturn(Mono.just(round));

        StepVerifier.create(roundQueryService.findCardByPlayerId(ObjectId.get().toString(), invalidPlayerId))
                .verifyErrorMessage(PLAYER_NOT_FOUND.params("id", invalidPlayerId).getMessage());
        verify(roundRepository).findById(anyString());
    }

    @Test
    void checkRoundIncompleteTest() {
        var round = RoundDocumentFactoryBot.builder().withDrawnNumbers(50).withIncompletedCard().build();

        StepVerifier.create(roundQueryService.checkRoundIncomplete(round))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual.cards().stream()
                            .map(Card::numbers)
                            .toList().get(0))
                            .hasSizeGreaterThan(actual.cards().stream()
                                    .map(Card::checkedNumbers)
                                    .toList().get(0).size());
                })
                .verifyComplete();
        verifyNoInteractions(roundRepository);
    }

    @Test
    void whenTryCheckRoundIncompleteTestAndRoundIsCompletedThenThrowError() {
        var round = RoundDocumentFactoryBot.builder().withDrawnNumbers(50).withCompletedCard().build();

        StepVerifier.create(roundQueryService.checkRoundIncomplete(round))
                .verifyError(RoundCompletedException.class);
        verifyNoInteractions(roundRepository);
    }

}
