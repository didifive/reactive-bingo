package com.reactivebingo.api.services;

import com.reactivebingo.api.documents.DrawnNumber;
import com.reactivebingo.api.documents.RoundDocument;
import com.reactivebingo.api.dtos.MailMessageDTO;
import com.reactivebingo.api.dtos.mappers.*;
import com.reactivebingo.api.exceptions.CardsLimitReachedException;
import com.reactivebingo.api.exceptions.PlayerInRoundException;
import com.reactivebingo.api.exceptions.RoundCompletedException;
import com.reactivebingo.api.exceptions.RoundStartedException;
import com.reactivebingo.api.mappers.CardDomainMapper;
import com.reactivebingo.api.mappers.CardDomainMapperImpl;
import com.reactivebingo.api.mappers.DrawnNumberDomainMapper;
import com.reactivebingo.api.mappers.DrawnNumberDomainMapperImpl;
import com.reactivebingo.api.repositories.RoundRepository;
import com.reactivebingo.api.services.queries.PlayerQueryService;
import com.reactivebingo.api.services.queries.RoundQueryService;
import com.reactivebingo.api.utils.factorybot.documents.PlayerDocumentFactoryBot;
import com.reactivebingo.api.utils.factorybot.documents.RoundDocumentFactoryBot;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RoundServiceTest {

    private final CardDomainMapper cardDomainMapper = new CardDomainMapperImpl();
    private final DrawnNumberDomainMapper drawnNumberDomainMapper = new DrawnNumberDomainMapperImpl();
    private final MailMapperDecorator mailMapper = new MailMapperImpl(new MailMapperImpl_(
            new CardMapperImpl()
            , new RoundMapperImpl(new CardMapperImpl(), new DrawnNumberMapperImpl())
            , new PlayerMapperImpl()
    ));
    @Mock
    private RoundRepository roundRepository;
    @Mock
    private RoundQueryService roundQueryService;
    @Mock
    private PlayerQueryService playerQueryService;
    @Mock
    private MailService mailService;
    private RoundService roundService;

    @BeforeEach
    void setup() {
        roundService = new RoundService(roundRepository, roundQueryService
                , cardDomainMapper, drawnNumberDomainMapper, mailMapper, playerQueryService, mailService);
    }

    @Test
    void saveTest() {
        var document = RoundDocumentFactoryBot.builder().build();
        when(roundRepository.save(any(RoundDocument.class))).thenAnswer(invocation -> {
            var round = invocation.getArgument(0, RoundDocument.class);
            return Mono.just(round.toBuilder()
                    .id(ObjectId.get().toString())
                    .createdAt(OffsetDateTime.now())
                    .updatedAt(OffsetDateTime.now())
                    .build());
        });

        StepVerifier.create(roundService.save(document))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual).usingRecursiveComparison()
                            .ignoringFields("id", "createdAt", "updatedAt")
                            .isEqualTo(document);
                    assertThat(actual.id()).isNotNull();
                    assertThat(actual.createdAt()).isNotNull();
                    assertThat(actual.updatedAt()).isNotNull();
                })
                .verifyComplete();
        verify(roundRepository).save(any(RoundDocument.class));
    }

    @Test
    void generateNewCardForPlayerInRound() {
        var playerId = ObjectId.get().toString();
        var roundId = ObjectId.get().toString();
        var round = RoundDocumentFactoryBot.builder().preUpdate(roundId).build();
        var player = PlayerDocumentFactoryBot.builder().preUpdate(playerId).build();
        when(roundQueryService.findById(anyString())).thenReturn(Mono.just(round));
        when(playerQueryService.findById(anyString())).thenReturn(Mono.just(player));
        when(roundRepository.save(any(RoundDocument.class))).thenReturn(Mono.just(round));

        StepVerifier.create(roundService.generateCard(roundId, playerId))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual.playerId()).isEqualTo(playerId);
                    assertThat(actual.numbers()).hasSize(20);
                    assertThat(actual.checkedNumbers()).isEmpty();
                })
                .verifyComplete();
        verify(roundQueryService).findById(anyString());
        verify(playerQueryService).findById(anyString());
        verify(roundRepository).save(any(RoundDocument.class));
    }

    @Test
    void whenTryToGenerateNewCardForPlayerWhoHasCardInRoundThenThrowError() {
        var playerId = ObjectId.get().toString();
        var roundId = ObjectId.get().toString();
        var round = RoundDocumentFactoryBot.builder().withCardToPlayer(playerId).build();
        var player = PlayerDocumentFactoryBot.builder().preUpdate(playerId).build();
        when(roundQueryService.findById(anyString())).thenReturn(Mono.just(round));
        when(playerQueryService.findById(anyString())).thenReturn(Mono.just(player));

        StepVerifier.create(roundService.generateCard(roundId, playerId))
                .verifyError(PlayerInRoundException.class);
        verify(roundQueryService).findById(anyString());
        verify(playerQueryService).findById(anyString());
        verifyNoInteractions(roundRepository);
    }

    @Test
    void whenTryToGenerateNewCardInRoundWhoStartedThenThrowError() {
        var playerId = ObjectId.get().toString();
        var roundId = ObjectId.get().toString();
        var round = RoundDocumentFactoryBot.builder().withDrawnNumbers(1).preUpdate(roundId).build();
        var player = PlayerDocumentFactoryBot.builder().preUpdate(playerId).build();
        when(roundQueryService.findById(anyString())).thenReturn(Mono.just(round));
        when(playerQueryService.findById(anyString())).thenReturn(Mono.just(player));

        StepVerifier.create(roundService.generateCard(roundId, playerId))
                .verifyError(RoundStartedException.class);
        verify(roundQueryService).findById(anyString());
        verify(playerQueryService).findById(anyString());
        verifyNoInteractions(roundRepository);
    }

    @Test
    void whenTryToGenerateNewCardToRoundWhoCardLimitReachedThenThrowError() {
        var playerId = ObjectId.get().toString();
        var roundId = ObjectId.get().toString();
        var round = RoundDocumentFactoryBot.builder().withCardsLimitsReached().preUpdate(roundId).build();
        var player = PlayerDocumentFactoryBot.builder().preUpdate(playerId).build();
        when(roundQueryService.findById(anyString())).thenReturn(Mono.just(round));
        when(playerQueryService.findById(anyString())).thenReturn(Mono.just(player));

        StepVerifier.create(roundService.generateCard(roundId, playerId))
                .verifyError(CardsLimitReachedException.class);
        verify(roundQueryService).findById(anyString());
        verify(playerQueryService).findById(anyString());
        verifyNoInteractions(roundRepository);
    }

    @Test
    void whenNumbersForCardExceedsRepeatLimitThenRetryGenerateNumbersForCard() {
        var playerId = ObjectId.get().toString();
        var roundId = ObjectId.get().toString();
        var round = RoundDocumentFactoryBot.builder().withCards(1).preUpdate(roundId).build();
        var player = PlayerDocumentFactoryBot.builder().preUpdate(playerId).build();
        when(roundQueryService.findById(anyString())).thenReturn(Mono.just(round));
        when(playerQueryService.findById(anyString())).thenReturn(Mono.just(player));
        when(roundRepository.save(any(RoundDocument.class))).thenReturn(Mono.just(round));

        StepVerifier.create(roundService.generateCard(roundId, playerId))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual.playerId()).isEqualTo(playerId);
                    assertThat(actual.numbers()).hasSize(20);
                    assertThat(actual.checkedNumbers()).isEmpty();
                })
                .verifyComplete();
        verify(roundQueryService).findById(anyString());
        verify(playerQueryService).findById(anyString());
        verify(roundRepository).save(any(RoundDocument.class));
    }

    @Test
    void drawNumberForRound() {
        var roundId = ObjectId.get().toString();
        var round = RoundDocumentFactoryBot.builder().withCards(5).preUpdate(roundId).build();
        when(roundQueryService.findById(anyString())).thenReturn(Mono.just(round));
        when(roundQueryService.checkRoundIncomplete(any(RoundDocument.class))).thenReturn(Mono.just(round));
        when(roundRepository.save(any(RoundDocument.class))).thenReturn(Mono.just(round));

        StepVerifier.create(roundService.drawNumber(roundId))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual).isExactlyInstanceOf(DrawnNumber.class);
                })
                .verifyComplete();
        verify(roundQueryService).findById(anyString());
        verify(roundQueryService, times(2)).checkRoundIncomplete(any(RoundDocument.class));
        verify(roundRepository).save(any(RoundDocument.class));
    }

    @Test
    void retryUntilNewDrawNumber() {
        var roundId = ObjectId.get().toString();
        var round = RoundDocumentFactoryBot.builder().withCards(50).withDrawnNumbers(95).preUpdate(roundId).build();
        when(roundQueryService.findById(anyString())).thenReturn(Mono.just(round));
        when(roundQueryService.checkRoundIncomplete(any(RoundDocument.class))).thenReturn(Mono.just(round));
        when(roundRepository.save(any(RoundDocument.class))).thenReturn(Mono.just(round));

        StepVerifier.create(roundService.drawNumber(roundId))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual).isExactlyInstanceOf(DrawnNumber.class);
                })
                .verifyComplete();
        verify(roundQueryService).findById(anyString());
        verify(roundQueryService, times(2)).checkRoundIncomplete(any(RoundDocument.class));
        verify(roundRepository).save(any(RoundDocument.class));
    }

    @Test
    void whenRoundIsFinishedThenSendEmail() throws InterruptedException {
        var playerId = ObjectId.get().toString();
        var roundId = ObjectId.get().toString();
        var round = RoundDocumentFactoryBot.builder().withCardToPlayer(playerId).withDrawnNumbers(96).preUpdate(roundId).build();
        var updatedRound = RoundDocumentFactoryBot.builder().withCardToPlayer(playerId).withDrawnNumbers(96).preUpdate(roundId).build();
        var player = PlayerDocumentFactoryBot.builder().preUpdate(playerId).build();
        var mailCaptor = ArgumentCaptor.forClass(MailMessageDTO.class);
        when(roundQueryService.findById(anyString())).thenReturn(Mono.just(round));
        when(roundQueryService.checkRoundIncomplete(any(RoundDocument.class))).thenReturn(Mono.just(round));
        when(roundRepository.save(any(RoundDocument.class))).thenReturn(Mono.just(updatedRound));
        doThrow(RoundCompletedException.class).when(roundQueryService).checkRoundIncomplete(updatedRound);
        when(playerQueryService.findById(anyString())).thenReturn(Mono.just(player));
        when(mailService.send(mailCaptor.capture())).thenReturn(Mono.empty());

        StepVerifier.create(roundService.drawNumber(roundId))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual).isExactlyInstanceOf(DrawnNumber.class);
                })
                .verifyComplete();
    }

}
