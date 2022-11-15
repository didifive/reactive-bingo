package com.reactivebingo.api.controllers.round;

import com.reactivebingo.api.controllers.AbstractControllerTest;
import com.reactivebingo.api.controllers.RoundController;
import com.reactivebingo.api.documents.RoundDocument;
import com.reactivebingo.api.dtos.mappers.CardMapperImpl;
import com.reactivebingo.api.dtos.mappers.DrawnNumberMapperImpl;
import com.reactivebingo.api.dtos.mappers.RoundMapperImpl;
import com.reactivebingo.api.dtos.requests.RoundRequestDTO;
import com.reactivebingo.api.dtos.responses.ErrorFieldResponseDTO;
import com.reactivebingo.api.dtos.responses.RoundResponseDTO;
import com.reactivebingo.api.dtos.responses.ProblemResponseDTO;
import com.reactivebingo.api.exceptions.EmailAlreadyUsedException;
import com.reactivebingo.api.services.RoundService;
import com.reactivebingo.api.services.queries.RoundQueryService;
import com.reactivebingo.api.utils.factorybot.dtos.requests.RoundRequestFactoryBot;
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
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static com.reactivebingo.api.utils.request.RequestBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ContextConfiguration(classes = {RoundMapperImpl.class
        , CardMapperImpl.class
        , DrawnNumberMapperImpl.class})
@WebFluxTest(RoundController.class)
class RoundControllerSaveTest extends AbstractControllerTest {

    @MockBean
    private RoundService roundService;
    @MockBean
    private RoundQueryService roundQueryService;
    private RequestBuilder<RoundResponseDTO> roundResponseDTORequestBuilder;
    private RequestBuilder<ProblemResponseDTO> responseDTORequestBuilder;

    private static Stream<Arguments> checkConstraintsTest() {
        return Stream.of(
                Arguments.of(RoundRequestFactoryBot.builder().blankName().build(), "name"),
                Arguments.of(RoundRequestFactoryBot.builder().longName().build(), "name"),
                Arguments.of(RoundRequestFactoryBot.builder().blankPrize().build(), "prize"),
                Arguments.of(RoundRequestFactoryBot.builder().longPrize().build(), "prize")
        );
    }

    @BeforeEach
    void setup() {
        roundResponseDTORequestBuilder = roundResponseDTORequestBuilder(applicationContext, "/rounds");
        responseDTORequestBuilder = problemResponseDTORequestBuilder(applicationContext, "/rounds");
    }

    @Test
    void saveTest() {
        when(roundService.save(any(RoundDocument.class))).thenAnswer(invocation -> {
            var document = invocation.getArgument(0, RoundDocument.class);
            return Mono.just(document.toBuilder()
                    .id(ObjectId.get().toString())
                    .createdAt(OffsetDateTime.now())
                    .updatedAt(OffsetDateTime.now())
                    .build());
        });
        var request = RoundRequestFactoryBot.builder().build();
        roundResponseDTORequestBuilder.uri(UriBuilder::build)
                .body(request)
                .generateRequestWithSimpleBody()
                .doPost()
                .httpStatusIsCreated()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.id()).isNotNull();
                    assertThat(response).usingRecursiveComparison()
                            .ignoringFields("id"
                                    , "drawnNumbers"
                                    , "cards"
                                    , "complete"
                                    , "createdAt"
                                    , "updatedAt")
                            .isEqualTo(request);
                    assertThat(response.drawnNumbers()).isEmpty();
                    assertThat(response.cards()).isEmpty();
                    assertThat(response.complete()).isFalse();
                });
    }

    @Test
    void whenTryUseEmailInUseThenReturnConflict() {
        when(roundService.save(any(RoundDocument.class))).thenReturn(Mono.error(new EmailAlreadyUsedException("")));
        var request = RoundRequestFactoryBot.builder().build();
        responseDTORequestBuilder.uri(UriBuilder::build)
                .body(request)
                .generateRequestWithSimpleBody()
                .doPost()
                .httpStatusIsBadRequest()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(BAD_REQUEST.value());
                });
    }

    @ParameterizedTest
    @MethodSource
    void checkConstraintsTest(final RoundRequestDTO request, final String field) {
        responseDTORequestBuilder.uri(UriBuilder::build)
                .body(request)
                .generateRequestWithSimpleBody()
                .doPost()
                .httpStatusIsBadRequest()
                .assertBody(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual.status()).isEqualTo(BAD_REQUEST.value());
                    assertThat(actual.fields().stream().map(ErrorFieldResponseDTO::name).toList()).contains(field);
                });
    }

}
