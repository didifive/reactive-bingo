package com.reactivebingo.api.controllers.round;

import com.reactivebingo.api.controllers.AbstractControllerTest;
import com.reactivebingo.api.controllers.RoundController;
import com.reactivebingo.api.dtos.mappers.*;
import com.reactivebingo.api.dtos.responses.RoundResponseDTO;
import com.reactivebingo.api.dtos.responses.ProblemResponseDTO;
import com.reactivebingo.api.exceptions.NotFoundException;
import com.reactivebingo.api.services.RoundService;
import com.reactivebingo.api.services.queries.RoundQueryService;
import com.reactivebingo.api.utils.factorybot.documents.RoundDocumentFactoryBot;
import com.reactivebingo.api.utils.request.RequestBuilder;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;

import static com.reactivebingo.api.utils.request.RequestBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ContextConfiguration(classes = {RoundMapperImpl.class
        ,CardMapperImpl.class
        , DrawnNumberMapperImpl.class})
@WebFluxTest(RoundController.class)
class RoundControllerFindByIdTest extends AbstractControllerTest {

    @MockBean
    private RoundService roundService;
    @MockBean
    private RoundQueryService roundQueryService;
    private RequestBuilder<RoundResponseDTO> roundResponseDTORequestBuilder;
    private RequestBuilder<ProblemResponseDTO> problemResponseDTORequestBuilder;

    @BeforeEach
    void setup(){
        roundResponseDTORequestBuilder = roundResponseDTORequestBuilder(applicationContext, "/rounds");
        problemResponseDTORequestBuilder = problemResponseDTORequestBuilder(applicationContext, "/rounds");
    }

    @Test
    void findByIdTest(){
        var player = RoundDocumentFactoryBot.builder().build();
        when(roundQueryService.findById(anyString())).thenReturn(Mono.just(player));
        roundResponseDTORequestBuilder
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .build(player.id()))
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsOk()
                .assertBody(response ->{
                    assertThat(response).isNotNull();
                    assertThat(response).usingRecursiveComparison()
                            .ignoringFields("complete","createdAt", "updatedAt")
                            .isEqualTo(player);
                });
    }


    @Test
    void whenTryToFindNonStoredRoundThenReturnNotFound(){
        when(roundQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        problemResponseDTORequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .build(ObjectId.get().toString()))
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsNotFound()
                .assertBody(response ->{
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(NOT_FOUND.value());
                });
    }

    @Test
    void whenTryUseNonValidIdThenReturnBadRequest(){
        problemResponseDTORequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .build(faker.lorem().word()))
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsBadRequest()
                .assertBody(response ->{
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(BAD_REQUEST.value());
                });
    }

}
