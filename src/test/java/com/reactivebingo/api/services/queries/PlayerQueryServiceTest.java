package com.reactivebingo.api.services.queries;

import com.github.javafaker.Faker;
import com.reactivebingo.api.documents.PlayerDocument;
import com.reactivebingo.api.dtos.requests.PlayerPageRequestDTO;
import com.reactivebingo.api.exceptions.NotFoundException;
import com.reactivebingo.api.repositories.PlayerRepository;
import com.reactivebingo.api.repositories.PlayerRepositoryImpl;
import com.reactivebingo.api.utils.factorybot.documents.PlayerDocumentFactoryBot;
import com.reactivebingo.api.utils.factorybot.dtos.requests.PlayerPageRequestFactoryBot;
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
import java.util.List;
import java.util.stream.Stream;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PlayerQueryServiceTest {

    private final static Faker faker = getFaker();
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PlayerRepositoryImpl playerRepositoryImpl;
    private PlayerQueryService playerQueryService;

    private static Stream<Arguments> findOnDemandTest() {
        var documents = Stream.generate(() -> PlayerDocumentFactoryBot.builder().build())
                .limit(faker.number().randomDigitNotZero())
                .toList();
        var pageRequest = PlayerPageRequestFactoryBot.builder().build();
        var total = faker.number().numberBetween(documents.size(), documents.size() * 3L);
        return Stream.of(
                Arguments.of(documents,
                        total,
                        pageRequest,
                        (total / pageRequest.limit()) + ((total % pageRequest.limit() > 0) ? 1 : 0)
                ),
                Arguments.of(new ArrayList<>(), 0L, PlayerPageRequestFactoryBot.builder().build(), 0L)
        );
    }

    @BeforeEach
    void setup() {
        playerQueryService = new PlayerQueryService(playerRepository, playerRepositoryImpl);
    }

    @Test
    void findByIdTest() {
        var document = PlayerDocumentFactoryBot.builder().build();
        when(playerRepository.findById(anyString())).thenReturn(Mono.just(document));

        StepVerifier.create(playerQueryService.findById(ObjectId.get().toString()))
                .assertNext(actual -> assertThat(actual).usingRecursiveComparison()
                        .ignoringFields("createdAt", "updatedAt")
                        .isEqualTo(document))
                .verifyComplete();
        verify(playerRepository).findById(anyString());
        verifyNoInteractions(playerRepositoryImpl);
    }

    @Test
    void whenTryToFindNonStoredPlayerByIdThenThrowError() {
        when(playerRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(playerQueryService.findById(ObjectId.get().toString()))
                .verifyError(NotFoundException.class);
        verify(playerRepository).findById(anyString());
        verifyNoInteractions(playerRepositoryImpl);
    }

    @Test
    void findByEmailTest() {
        var document = PlayerDocumentFactoryBot.builder().build();
        when(playerRepository.findByEmail(anyString())).thenReturn(Mono.just(document));

        StepVerifier.create(playerQueryService.findByEmail(faker.internet().emailAddress()))
                .assertNext(actual -> assertThat(actual).usingRecursiveComparison()
                        .ignoringFields("createdAt", "updatedAt")
                        .isEqualTo(document))
                .verifyComplete();
        verify(playerRepository).findByEmail(anyString());
        verifyNoInteractions(playerRepositoryImpl);
    }

    @Test
    void whenTryToFindNonStoredPlayerByEmailThenThrowError() {
        when(playerRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(playerQueryService.findByEmail(faker.internet().emailAddress()))
                .verifyError(NotFoundException.class);
        verify(playerRepository).findByEmail(anyString());
        verifyNoInteractions(playerRepositoryImpl);
    }

    @MethodSource
    @ParameterizedTest
    void findOnDemandTest(final List<PlayerDocument> documents, final Long total,
                          final PlayerPageRequestDTO pageRequest, final Long expectTotalPages) {
        when(playerRepositoryImpl.findOnDemand(any(PlayerPageRequestDTO.class))).thenReturn(Flux.fromIterable(documents));
        when(playerRepositoryImpl.count(any(PlayerPageRequestDTO.class))).thenReturn(Mono.just(total));

        StepVerifier.create(playerQueryService.findOnDemand(pageRequest))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat((List<PlayerDocument>) actual.content()).containsExactlyInAnyOrderElementsOf(documents);
                    assertThat(actual.totalItems()).isEqualTo(total);
                    assertThat(actual.totalPages()).isEqualTo(expectTotalPages);
                })
                .verifyComplete();
    }

}
