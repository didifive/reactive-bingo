package com.reactivebingo.api.controllers.player;

import com.reactivebingo.api.controllers.AbstractControllerTest;
import com.reactivebingo.api.controllers.PlayerController;
import com.reactivebingo.api.documents.PlayerDocument;
import com.reactivebingo.api.dtos.mappers.PlayerMapperImpl;
import com.reactivebingo.api.dtos.requests.PlayerRequestDTO;
import com.reactivebingo.api.dtos.responses.ErrorFieldResponseDTO;
import com.reactivebingo.api.dtos.responses.PlayerResponseDTO;
import com.reactivebingo.api.dtos.responses.ProblemResponseDTO;
import com.reactivebingo.api.exceptions.EmailAlreadyUsedException;
import com.reactivebingo.api.services.PlayerService;
import com.reactivebingo.api.services.queries.PlayerQueryService;
import com.reactivebingo.api.utils.factorybot.dtos.requests.PlayerRequestFactoryBot;
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

import static com.reactivebingo.api.utils.request.RequestBuilder.playerResponseDTORequestBuilder;
import static com.reactivebingo.api.utils.request.RequestBuilder.problemResponseDTORequestBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ContextConfiguration(classes = {PlayerMapperImpl.class})
@WebFluxTest(PlayerController.class)
class PlayerControllerSaveTest extends AbstractControllerTest {

    @MockBean
    private PlayerService playerService;
    @MockBean
    private PlayerQueryService playerQueryService;
    private RequestBuilder<PlayerResponseDTO> playerResponseDTORequestBuilder;
    private RequestBuilder<ProblemResponseDTO> problemResponseDTORequestBuilder;

    private static Stream<Arguments> checkConstraintsTest() {
        return Stream.of(
                Arguments.of(PlayerRequestFactoryBot.builder().blankName().build(), "name"),
                Arguments.of(PlayerRequestFactoryBot.builder().longName().build(), "name"),
                Arguments.of(PlayerRequestFactoryBot.builder().blankEmail().build(), "email"),
                Arguments.of(PlayerRequestFactoryBot.builder().longEmail().build(), "email"),
                Arguments.of(PlayerRequestFactoryBot.builder().invalidEmail().build(), "email")
        );
    }

    @BeforeEach
    void setup() {
        playerResponseDTORequestBuilder = playerResponseDTORequestBuilder(applicationContext, "/players");
        problemResponseDTORequestBuilder = problemResponseDTORequestBuilder(applicationContext, "/players");
    }

    @Test
    void saveTest() {
        when(playerService.save(any(PlayerDocument.class))).thenAnswer(invocation -> {
            var document = invocation.getArgument(0, PlayerDocument.class);
            return Mono.just(document.toBuilder()
                    .id(ObjectId.get().toString())
                    .createdAt(OffsetDateTime.now())
                    .updatedAt(OffsetDateTime.now())
                    .build());
        });
        var request = PlayerRequestFactoryBot.builder().build();
        playerResponseDTORequestBuilder.uri(UriBuilder::build)
                .body(request)
                .generateRequestWithSimpleBody()
                .doPost()
                .httpStatusIsCreated()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.id()).isNotNull();
                    assertThat(response).usingRecursiveComparison()
                            .ignoringFields("id", "createdAt", "updatedAt")
                            .isEqualTo(request);
                });
    }

    @Test
    void whenTryUseEmailInUseThenReturnConflict() {
        when(playerService.save(any(PlayerDocument.class))).thenReturn(Mono.error(new EmailAlreadyUsedException("")));
        var request = PlayerRequestFactoryBot.builder().build();
        problemResponseDTORequestBuilder.uri(UriBuilder::build)
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
    void checkConstraintsTest(final PlayerRequestDTO request, final String field) {
        problemResponseDTORequestBuilder.uri(UriBuilder::build)
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
