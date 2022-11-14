package com.reactivebingo.api.repositories;

import com.github.javafaker.Faker;
import com.reactivebingo.api.config.EmbeddedMongoDBConfig;
import com.reactivebingo.api.documents.PlayerDocument;
import com.reactivebingo.api.dtos.requests.PlayerPageRequestDTO;
import com.reactivebingo.api.utils.factorybot.documents.PlayerDocumentFactoryBot;
import com.reactivebingo.api.utils.factorybot.dtos.requests.PlayerPageRequestFactoryBot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.reactivebingo.api.dtos.enums.PlayerSortBy.EMAIL;
import static com.reactivebingo.api.dtos.enums.PlayerSortBy.NAME;
import static com.reactivebingo.api.dtos.enums.SortDirection.ASC;
import static com.reactivebingo.api.dtos.enums.SortDirection.DESC;
import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("test")
@DataMongoTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmbeddedMongoDBConfig.class})
class PlayerRepositoryImplTest {

    private final Faker faker = getFaker();
    private final List<PlayerDocument> storedDocuments = new ArrayList<>();
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PlayerRepositoryImpl playerRepositoryImpl;

    private static Stream<Arguments> verifySort() {
        return Stream.of(
                Arguments.of(
                        PlayerPageRequestDTO.builder().sortBy(NAME).sortDirection(ASC).build(),
                        Comparator.comparing(PlayerDocument::name)
                ),
                Arguments.of(
                        PlayerPageRequestDTO.builder().sortBy(NAME).sortDirection(DESC).build(),
                        Comparator.comparing(PlayerDocument::name).reversed()
                ),
                Arguments.of(
                        PlayerPageRequestDTO.builder().sortBy(EMAIL).sortDirection(ASC).build(),
                        Comparator.comparing(PlayerDocument::email)
                ),
                Arguments.of(
                        PlayerPageRequestDTO.builder().sortBy(EMAIL).sortDirection(DESC).build(),
                        Comparator.comparing(PlayerDocument::email).reversed()
                )
        );
    }

    @BeforeEach
    void setup() {
        var players = Stream.generate(() -> PlayerDocumentFactoryBot.builder().build()).limit(15).toList();
        storedDocuments.addAll(Objects.requireNonNull(playerRepository.saveAll(players).collectList().block()));
    }

    @Test
    void combineAllOptions() {
        var selectedPlayer = storedDocuments.get(faker.number().numberBetween(0, storedDocuments.size()));
        var sentence = faker.bool().bool() ? selectedPlayer.name().substring(1, 2) : selectedPlayer.email().substring(1, 2);
        var pageRequest = PlayerPageRequestFactoryBot.builder().build()
                .toBuilder()
                .sentence(sentence)
                .limit(20)
                .page(0L)
                .build();
        StepVerifier.create(playerRepositoryImpl.findOnDemand(pageRequest))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> assertThat(actual).isNotEmpty()).verifyComplete();
    }

    @Test
    void checkFindOnDemandFilterBySentenceTest() {
        var selectedPlayer = storedDocuments.get(faker.number().numberBetween(0, storedDocuments.size()));
        var sentence = faker.bool().bool() ? selectedPlayer.name().substring(1, 2) : selectedPlayer.email().substring(1, 2);
        var pageRequest = PlayerPageRequestDTO.builder().sentence(sentence).build();
        StepVerifier.create(playerRepositoryImpl.findOnDemand(pageRequest))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> {
                    var actualList = new ArrayList<>(actual);
                    var expectSize = storedDocuments.stream()
                            .filter(p -> p.name().contains(sentence) || p.email().contains(sentence))
                            .count();
                    assertAll(
                            "verify find ondemand filter"
                            , () -> assertThat(actual).hasSize((int) expectSize)
                            , () -> assertThat(actualList).isSortedAccordingTo(Comparator.comparing(PlayerDocument::name))
                    );
                })
                .verifyComplete();
    }

    @Test
    void checkCountFilterBySentenceTest() {
        var selectedPlayer = storedDocuments.get(faker.number().numberBetween(0, storedDocuments.size()));
        var sentence = faker.bool().bool() ? selectedPlayer.name().substring(1, 2) : selectedPlayer.email().substring(1, 2);
        var pageRequest = PlayerPageRequestDTO.builder().sentence(sentence).build();
        StepVerifier.create(playerRepositoryImpl.count(pageRequest))
                .assertNext(actual -> {
                    var expectSize = storedDocuments.stream()
                            .filter(p -> p.name().contains(sentence) || p.email().contains(sentence))
                            .count();
                    assertThat(actual).isEqualTo(expectSize);
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource
    void verifySort(final PlayerPageRequestDTO pageRequest, final Comparator<PlayerDocument> comparator) {
        StepVerifier.create(playerRepositoryImpl.findOnDemand(pageRequest))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> {
                    var actualList = new ArrayList<>(actual);
                    assertThat(actualList).isSortedAccordingTo(comparator);
                })
                .verifyComplete();
    }

    @AfterEach
    void teardown() {
        playerRepository.deleteAll().block();
        storedDocuments.clear();
    }
}
