package com.reactivebingo.api.controllers.round;

import com.reactivebingo.api.controllers.AbstractControllerTest;
import com.reactivebingo.api.controllers.RoundController;
import com.reactivebingo.api.documents.Card;
import com.reactivebingo.api.dtos.CardDTO;
import com.reactivebingo.api.dtos.mappers.CardMapperImpl;
import com.reactivebingo.api.dtos.mappers.DrawnNumberMapperImpl;
import com.reactivebingo.api.dtos.mappers.RoundMapperImpl;
import com.reactivebingo.api.dtos.responses.ErrorFieldResponseDTO;
import com.reactivebingo.api.dtos.responses.ProblemResponseDTO;
import com.reactivebingo.api.services.RoundService;
import com.reactivebingo.api.services.queries.RoundQueryService;
import com.reactivebingo.api.utils.request.RequestBuilder;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static com.reactivebingo.api.utils.request.RequestBuilder.cardDTORequestBuilder;
import static com.reactivebingo.api.utils.request.RequestBuilder.problemResponseDTORequestBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ContextConfiguration(classes = {RoundMapperImpl.class
        , CardMapperImpl.class
        , DrawnNumberMapperImpl.class})
@WebFluxTest(RoundController.class)
class RoundControllerFindCardTest extends AbstractControllerTest {

    @MockBean
    private RoundService roundService;
    @MockBean
    private RoundQueryService roundQueryService;
    private RequestBuilder<CardDTO> cardDTORequestBuilder;
    private RequestBuilder<ProblemResponseDTO> responseDTORequestBuilder;

    private static Stream<Arguments> checkConstraintsTest() {
        return Stream.of(
                Arguments.of(getFaker().ancient().hero(), "id"),
                Arguments.of(getFaker().ancient().titan(), "playerId")
        );
    }

    private Set<Short> getRandomNumbersSet(int limit) {
        Set<Short> randomNumbers = new HashSet<>();
        while (randomNumbers.size() < limit) {
            randomNumbers.add(Short.parseShort(String.valueOf(getFaker().number().numberBetween(0, 99))));
        }
        return randomNumbers;
    }

    @BeforeEach
    void setup() {
        cardDTORequestBuilder = cardDTORequestBuilder(applicationContext, "/rounds");
        responseDTORequestBuilder = problemResponseDTORequestBuilder(applicationContext, "/rounds");
    }

    @Test
    void findCardTest() {
        var id = ObjectId.get().toString();
        var playerId = ObjectId.get().toString();
        when(roundQueryService.findCardByPlayerId(id, playerId))
                .thenReturn(Mono.just(Card.builder()
                        .playerId(playerId)
                        .numbers(getRandomNumbersSet(20))
                        .checkedNumbers(new HashSet<>())
                        .createdAt(OffsetDateTime.now())
                        .build()));
        cardDTORequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}", "cards", "get", "{playerId}")
                        .build(id, playerId))
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsOk()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.playerId()).isNotNull();
                    assertThat(response.numbers()).hasSize(20);
                    assertThat(response.checkedNumbers()).isEmpty();
                    assertThat(response.complete()).isFalse();
                    assertThat(response.createdAt()).isBeforeOrEqualTo(OffsetDateTime.now());
                });
    }

    @ParameterizedTest
    @MethodSource
    void checkConstraintsTest(final String id, final String field) {
        responseDTORequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}", "cards", "get", "{playerId}")
                        .build(field == "id" ? id : ObjectId.get().toString()
                                , field == "playerId" ? id : ObjectId.get().toString()))
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsBadRequest()
                .assertBody(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual.status()).isEqualTo(BAD_REQUEST.value());
                    assertThat(actual.fields().stream()
                            .map(ErrorFieldResponseDTO::name).toList())
                            .containsAnyOf("id", "playerId");
                });
    }

}
