package com.reactivebingo.api.controllers.player;

import com.reactivebingo.api.controllers.AbstractControllerTest;
import com.reactivebingo.api.controllers.PlayerController;
import com.reactivebingo.api.dtos.mappers.PlayerMapperImpl;
import com.reactivebingo.api.dtos.responses.ErrorFieldResponseDTO;
import com.reactivebingo.api.dtos.responses.ProblemResponseDTO;
import com.reactivebingo.api.exceptions.NotFoundException;
import com.reactivebingo.api.services.PlayerService;
import com.reactivebingo.api.services.queries.PlayerQueryService;
import com.reactivebingo.api.utils.request.RequestBuilder;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;

import static com.reactivebingo.api.utils.request.RequestBuilder.noContentRequestBuilder;
import static com.reactivebingo.api.utils.request.RequestBuilder.problemResponseDTORequestBuilder;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {PlayerMapperImpl.class})
@WebFluxTest(PlayerController.class)
class PlayerControllerDeleteTest extends AbstractControllerTest {

    @MockBean
    private PlayerService playerService;
    @MockBean
    private PlayerQueryService playerQueryService;
    private RequestBuilder<Void> noContentRequestBuilder;
    private RequestBuilder<ProblemResponseDTO> problemResponseDTORequestBuilder;

    @BeforeEach
    void setup(){
        noContentRequestBuilder = noContentRequestBuilder(applicationContext, "/players");
        problemResponseDTORequestBuilder = problemResponseDTORequestBuilder(applicationContext, "/players");
    }

    @Test
    void deleteTest(){
        when(playerService.delete(anyString())).thenReturn(Mono.empty());
        noContentRequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .build(ObjectId.get().toString()))
                .generateRequestWithoutBody()
                .doDelete()
                .httpStatusIsNoContent();
    }

    @Test
    void whenTryToDeleteNoStoredPlayerThenReturnNotFound(){
        when(playerService.delete(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        problemResponseDTORequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .build(ObjectId.get().toString()))
                .generateRequestWithSimpleBody()
                .doDelete()
                .httpStatusIsNotFound()
                .assertBody(actual ->{
                    assertThat(actual).isNotNull();
                    assertThat(actual.status()).isEqualTo(NOT_FOUND.value());
                });
    }

    @Test
    void whenTryUseInvalidIdThenReturnBadRequest(){
        when(playerService.delete(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        problemResponseDTORequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .build(faker.lorem().word()))
                .generateRequestWithSimpleBody()
                .doDelete()
                .httpStatusIsBadRequest()
                .assertBody(actual ->{
                    assertThat(actual).isNotNull();
                    assertThat(actual.status()).isEqualTo(BAD_REQUEST.value());
                    assertThat(actual.fields().stream().map(ErrorFieldResponseDTO::name).toList()).contains("id");
                });
    }

}
