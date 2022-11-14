package com.reactivebingo.api.repositories;

import com.github.javafaker.Faker;
import com.reactivebingo.api.config.EmbeddedMongoDBConfig;
import com.reactivebingo.api.documents.RoundDocument;
import com.reactivebingo.api.dtos.requests.RoundPageRequestDTO;
import com.reactivebingo.api.utils.factorybot.documents.RoundDocumentFactoryBot;
import com.reactivebingo.api.utils.factorybot.dtos.requests.RoundPageRequestFactoryBot;
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

import static com.reactivebingo.api.dtos.enums.RoundSortBy.*;
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
class RoundRepositoryImplTest {

    private final Faker faker = getFaker();
    private final List<RoundDocument> storedDocuments = new ArrayList<>();
    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private RoundRepositoryImpl roundRepositoryImpl;

    private static Stream<Arguments> verifySort() {
        return Stream.of(
                Arguments.of(
                        RoundPageRequestDTO.builder().sortBy(NAME).sortDirection(ASC).build(),
                        Comparator.comparing(RoundDocument::name)
                ),
                Arguments.of(
                        RoundPageRequestDTO.builder().sortBy(NAME).sortDirection(DESC).build(),
                        Comparator.comparing(RoundDocument::name).reversed()
                ),
                Arguments.of(
                        RoundPageRequestDTO.builder().sortBy(PRIZE).sortDirection(ASC).build(),
                        Comparator.comparing(RoundDocument::prize)
                ),
                Arguments.of(
                        RoundPageRequestDTO.builder().sortBy(PRIZE).sortDirection(DESC).build(),
                        Comparator.comparing(RoundDocument::prize).reversed()
                ),
                Arguments.of(
                        RoundPageRequestDTO.builder().sortBy(DATE).sortDirection(ASC).build(),
                        Comparator.comparing(RoundDocument::createdAt)
                ),
                Arguments.of(
                        RoundPageRequestDTO.builder().sortBy(DATE).sortDirection(DESC).build(),
                        Comparator.comparing(RoundDocument::createdAt).reversed()
                )
        );
    }

    @BeforeEach
    void setup() {
        var rounds = Stream.generate(() -> RoundDocumentFactoryBot.builder().build()).limit(15).toList();
        storedDocuments.addAll(Objects.requireNonNull(roundRepository.saveAll(rounds).collectList().block()));
    }

    @Test
    void combineAllOptions() {
        var selectedRound = storedDocuments.get(faker.number().numberBetween(0, storedDocuments.size()));
        var sentence = faker.bool().bool() ? selectedRound.name().substring(1, 2) : selectedRound.prize().substring(1, 2);
        var pageRequest = RoundPageRequestFactoryBot.builder().build()
                .toBuilder()
                .sentence(sentence)
                .limit(20)
                .page(0L)
                .build();
        StepVerifier.create(roundRepositoryImpl.findOnDemand(pageRequest))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> assertThat(actual).isNotEmpty()).verifyComplete();
    }

    @Test
    void checkFindOnDemandFilterBySentenceTest() {
        var selectedRound = storedDocuments.get(faker.number().numberBetween(0, storedDocuments.size()));
        var sentence = faker.bool().bool() ? selectedRound.name().substring(1, 2) : selectedRound.prize().substring(1, 2);
        var pageRequest = RoundPageRequestDTO.builder().sentence(sentence).build();
        StepVerifier.create(roundRepositoryImpl.findOnDemand(pageRequest))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> {
                    var actualList = new ArrayList<>(actual);
                    var expectSize = storedDocuments.stream()
                            .filter(r -> r.name().contains(sentence) || r.prize().contains(sentence))
                            .count();
                    assertAll(
                            "verify find ondemand filter"
                            , () -> assertThat(actual).hasSize((int) expectSize)
                            , () -> assertThat(actualList).isSortedAccordingTo(Comparator.comparing(RoundDocument::name))
                    );
                })
                .verifyComplete();
    }

    @Test
    void checkCountFilterBySentenceTest() {
        var selectedRound = storedDocuments.get(faker.number().numberBetween(0, storedDocuments.size()));
        var sentence = faker.bool().bool() ? selectedRound.name().substring(1, 2) : selectedRound.prize().substring(1, 2);
        var pageRequest = RoundPageRequestDTO.builder().sentence(sentence).build();
        StepVerifier.create(roundRepositoryImpl.count(pageRequest))
                .assertNext(actual -> {
                    var expectSize = storedDocuments.stream()
                            .filter(r -> r.name().contains(sentence) || r.prize().contains(sentence))
                            .count();
                    assertThat(actual).isEqualTo(expectSize);
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource
    void verifySort(final RoundPageRequestDTO pageRequest, final Comparator<RoundDocument> comparator) {
        StepVerifier.create(roundRepositoryImpl.findOnDemand(pageRequest))
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
        roundRepository.deleteAll().block();
        storedDocuments.clear();
    }
}
