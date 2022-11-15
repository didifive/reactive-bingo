package com.reactivebingo.api.controllers.player;

import com.reactivebingo.api.controllers.AbstractControllerTest;
import com.reactivebingo.api.controllers.PlayerController;
import com.reactivebingo.api.dtos.mappers.PlayerMapperImpl;
import com.reactivebingo.api.dtos.responses.PlayerResponseDTO;
import com.reactivebingo.api.dtos.responses.ProblemResponseDTO;
import com.reactivebingo.api.exceptions.NotFoundException;
import com.reactivebingo.api.services.PlayerService;
import com.reactivebingo.api.services.queries.PlayerQueryService;
import com.reactivebingo.api.utils.factorybot.documents.PlayerDocumentFactoryBot;
import com.reactivebingo.api.utils.request.RequestBuilder;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;

import static com.reactivebingo.api.utils.request.RequestBuilder.playerResponseDTORequestBuilder;
import static com.reactivebingo.api.utils.request.RequestBuilder.problemResponseDTORequestBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ContextConfiguration(classes = {PlayerMapperImpl.class})
@WebFluxTest(PlayerController.class)
class PlayerControllerFindByIdTest extends AbstractControllerTest {

    @MockBean
    private PlayerService playerService;
    @MockBean
    private PlayerQueryService playerQueryService;
    private RequestBuilder<PlayerResponseDTO> playerResponseDTORequestBuilder;
    private RequestBuilder<ProblemResponseDTO> problemResponseDTORequestBuilder;

    @BeforeEach
    void setup(){
        playerResponseDTORequestBuilder = playerResponseDTORequestBuilder(applicationContext, "/players");
        problemResponseDTORequestBuilder = problemResponseDTORequestBuilder(applicationContext, "/players");
    }

    @Test
    void findByIdTest(){
        var player = PlayerDocumentFactoryBot.builder().build();
        when(playerQueryService.findById(anyString())).thenReturn(Mono.just(player));
        playerResponseDTORequestBuilder
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .build(player.id()))
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsOk()
                .assertBody(response ->{
                    assertThat(response).isNotNull();
                    assertThat(response).usingRecursiveComparison()
                            .ignoringFields("createdAt", "updatedAt")
                            .isEqualTo(player);
                });
    }


    @Test
    void whenTryToFindNonStoredPlayerThenReturnNotFound(){
        when(playerQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));
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
