package com.reactivebingo.api.services;

import com.reactivebingo.api.documents.PlayerDocument;
import com.reactivebingo.api.exceptions.EmailAlreadyUsedException;
import com.reactivebingo.api.exceptions.NotFoundException;
import com.reactivebingo.api.repositories.PlayerRepository;
import com.reactivebingo.api.services.queries.PlayerQueryService;
import com.reactivebingo.api.utils.factorybot.documents.PlayerDocumentFactoryBot;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PlayerQueryService playerQueryService;
    private PlayerService playerService;

    private static Stream<Arguments> updateTest() {
        var document = PlayerDocumentFactoryBot.builder().build();
        var storedDocument = PlayerDocumentFactoryBot.builder().preUpdate(document.id()).build();
        return Stream.of(
                Arguments.of(document, Mono.error(new NotFoundException("")), storedDocument),
                Arguments.of(document, Mono.just(storedDocument), storedDocument)
        );
    }

    @BeforeEach
    void setup() {
        playerService = new PlayerService(playerRepository, playerQueryService);
    }

    @Test
    void saveTest() {
        var document = PlayerDocumentFactoryBot.builder().build();
        when(playerQueryService.findByEmail(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        when(playerRepository.save(any(PlayerDocument.class))).thenAnswer(invocation -> {
            var player = invocation.getArgument(0, PlayerDocument.class);
            return Mono.just(player.toBuilder()
                    .id(ObjectId.get().toString())
                    .createdAt(OffsetDateTime.now())
                    .updatedAt(OffsetDateTime.now())
                    .build());
        });

        StepVerifier.create(playerService.save(document))
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
        verify(playerRepository).save(any(PlayerDocument.class));
        verify(playerQueryService).findByEmail(anyString());
    }

    @Test
    void whenTryToSavePlayerWithExistingEmailThenThrowError() {
        var document = PlayerDocumentFactoryBot.builder().build();
        when(playerQueryService.findByEmail(anyString())).thenReturn(Mono.just(PlayerDocumentFactoryBot.builder().build()));

        StepVerifier.create(playerService.save(document))
                .verifyError(EmailAlreadyUsedException.class);
        verify(playerRepository, times(0)).save(any(PlayerDocument.class));
        verify(playerQueryService).findByEmail(anyString());
    }

    @MethodSource
    @ParameterizedTest
    void updateTest(final PlayerDocument toUpdate, final Mono<PlayerDocument> mockFindByEmail, final PlayerDocument mockFindById) {
        when(playerQueryService.findByEmail(anyString())).thenReturn(mockFindByEmail);
        when(playerQueryService.findById(anyString())).thenReturn(Mono.just(mockFindById));
        when(playerRepository.save(any(PlayerDocument.class))).thenAnswer(invocation -> {
            var player = invocation.getArgument(0, PlayerDocument.class);
            return Mono.just(player.toBuilder()
                    .updatedAt(OffsetDateTime.now())
                    .build());
        });

        StepVerifier.create(playerService.update(toUpdate))
                .assertNext(actual -> {
                    assertThat(actual).usingRecursiveComparison()
                            .ignoringFields("createdAt", "updatedAt")
                            .isNotEqualTo(mockFindById);
                    assertThat(actual).usingRecursiveComparison()
                            .ignoringFields("createdAt", "updatedAt")
                            .isEqualTo(toUpdate);
                })
                .verifyComplete();
        verify(playerQueryService).findByEmail(anyString());
        verify(playerRepository).save(any(PlayerDocument.class));
        verify(playerQueryService).findById(anyString());
    }

    @Test
    void whenTryToUpdatePlayerWithEmailUsedByOtherThenThrowError() {
        var document = PlayerDocumentFactoryBot.builder().build();
        when(playerQueryService.findByEmail(anyString())).thenReturn(Mono.just(PlayerDocumentFactoryBot.builder().build()));

        StepVerifier.create(playerService.update(document))
                .verifyError(EmailAlreadyUsedException.class);
        verify(playerQueryService).findByEmail(anyString());
        verify(playerQueryService, times(0)).findById(anyString());
        verify(playerRepository, times(0)).save(any(PlayerDocument.class));
    }

    @Test
    void whenTryToUpdatePlayerNonStoredThenThrowError() {
        var document = PlayerDocumentFactoryBot.builder().build();
        when(playerQueryService.findByEmail(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        when(playerQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));

        StepVerifier.create(playerService.update(document))
                .verifyError(NotFoundException.class);
        verify(playerQueryService).findByEmail(anyString());
        verify(playerQueryService).findById(anyString());
        verify(playerRepository, times(0)).save(any(PlayerDocument.class));
    }

    @Test
    void deleteTest() {
        when(playerQueryService.findById(anyString())).thenReturn(Mono.just(PlayerDocumentFactoryBot.builder().build()));
        when(playerRepository.delete(any(PlayerDocument.class))).thenReturn(Mono.empty());

        StepVerifier.create(playerService.delete(ObjectId.get().toString()))
                .verifyComplete();
        verify(playerRepository).delete(any(PlayerDocument.class));
        verify(playerQueryService).findById(anyString());
    }

    @Test
    void whenTryToDeleteNonStoredPlayerThenThrowError() {
        when(playerQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));

        StepVerifier.create(playerService.delete(ObjectId.get().toString()))
                .verifyError(NotFoundException.class);
        verify(playerQueryService).findById(anyString());
        verifyNoInteractions(playerRepository);
    }
}
