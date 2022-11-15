package com.reactivebingo.api.controllers.round;

import com.reactivebingo.api.controllers.AbstractControllerTest;
import com.reactivebingo.api.controllers.RoundController;
import com.reactivebingo.api.documents.Card;
import com.reactivebingo.api.documents.DrawnNumber;
import com.reactivebingo.api.dtos.CardDTO;
import com.reactivebingo.api.dtos.DrawnNumberDTO;
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
import static com.reactivebingo.api.utils.request.RequestBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ContextConfiguration(classes = {RoundMapperImpl.class
        , CardMapperImpl.class
        , DrawnNumberMapperImpl.class})
@WebFluxTest(RoundController.class)
class RoundControllerDrawNumberTest extends AbstractControllerTest {

    @MockBean
    private RoundService roundService;
    @MockBean
    private RoundQueryService roundQueryService;
    private RequestBuilder<DrawnNumberDTO> drawnNumberDTORequestBuilder;
    private RequestBuilder<ProblemResponseDTO> responseDTORequestBuilder;

    @BeforeEach
    void setup() {
        drawnNumberDTORequestBuilder = drawnNumberDTORequestBuilder(applicationContext, "/rounds");
        responseDTORequestBuilder = problemResponseDTORequestBuilder(applicationContext, "/rounds");
    }

    @Test
    void drawNumberTest() {
        var id = ObjectId.get().toString();
        when(roundService.drawNumber(id))
                .thenReturn(Mono.just(DrawnNumber.builder()
                        .number(Short.parseShort(String.valueOf(getFaker().number().numberBetween(0, 99))))
                        .drawnAt(OffsetDateTime.now())
                        .build()));
        drawnNumberDTORequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}", "draw-number")
                        .build(id))
                .generateRequestWithSimpleBody()
                .doPost()
                .httpStatusIsCreated()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.number()).isNotNegative();
                    assertThat(response.drawnAt()).isBeforeOrEqualTo(OffsetDateTime.now());
                });
    }

    @Test
    void checkConstraintsTest() {
        responseDTORequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}", "draw-number")
                        .build(getFaker().beer().name()))
                .generateRequestWithSimpleBody()
                .doPost()
                .httpStatusIsBadRequest()
                .assertBody(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual.status()).isEqualTo(BAD_REQUEST.value());
                    assertThat(actual.fields().stream()
                            .map(ErrorFieldResponseDTO::name).toList())
                            .contains("id");
                });
    }

}
