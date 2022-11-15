package com.reactivebingo.api.controllers.player;

import com.reactivebingo.api.controllers.AbstractControllerTest;
import com.reactivebingo.api.controllers.PlayerController;
import com.reactivebingo.api.documents.Page;
import com.reactivebingo.api.dtos.mappers.PlayerMapperImpl;
import com.reactivebingo.api.dtos.requests.PlayerPageRequestDTO;
import com.reactivebingo.api.dtos.responses.ErrorFieldResponseDTO;
import com.reactivebingo.api.dtos.responses.PageResponseDTO;
import com.reactivebingo.api.dtos.responses.ProblemResponseDTO;
import com.reactivebingo.api.services.PlayerService;
import com.reactivebingo.api.services.queries.PlayerQueryService;
import com.reactivebingo.api.utils.factorybot.documents.PlayerPageDocumentFactoryBot;
import com.reactivebingo.api.utils.factorybot.dtos.requests.PlayerPageRequestFactoryBot;
import com.reactivebingo.api.utils.request.RequestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.reactivebingo.api.utils.request.RequestBuilder.playerPageResponseDTORequestBuilder;
import static com.reactivebingo.api.utils.request.RequestBuilder.problemResponseDTORequestBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ContextConfiguration(classes = {PlayerMapperImpl.class})
@WebFluxTest(PlayerController.class)
class PlayerControllerFindOnDemandTest extends AbstractControllerTest {

    @MockBean
    private PlayerService userService;
    @MockBean
    private PlayerQueryService playerQueryService;
    private RequestBuilder<PageResponseDTO> pageResponseDTORequestBuilder;
    private RequestBuilder<ProblemResponseDTO> problemResponseDTORequestBuilder;

    @BeforeEach
    void setup(){
        pageResponseDTORequestBuilder = playerPageResponseDTORequestBuilder(applicationContext, "/players");
        problemResponseDTORequestBuilder = problemResponseDTORequestBuilder(applicationContext, "/players");
    }

    private static Stream<Page> findOnDemandTest(){
        return Stream.of(
                PlayerPageDocumentFactoryBot.builder().build(),
                PlayerPageDocumentFactoryBot.builder().emptyPage().build()
        );
    }

    @ParameterizedTest
    @MethodSource
    void findOnDemandTest(final Page pageDocument){
        var queryParams = PlayerPageRequestFactoryBot.builder().build();
        when(playerQueryService.findOnDemand(any(PlayerPageRequestDTO.class))).thenReturn(Mono.just(pageDocument));
        pageResponseDTORequestBuilder.uri(uriBuilder -> uriBuilder
                        .queryParam("page", queryParams.page())
                        .queryParam("limit", queryParams.limit())
                        .queryParam("sentence", queryParams.sentence())
                        .queryParam("sortBy", queryParams.sortBy())
                        .queryParam("sortDirection", queryParams.sortDirection())
                        .build())
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsOk()
                .assertBody(response -> assertThat(response).isNotNull());
    }

    private static Stream<Arguments> checkConstraintsTest(){
        var invalidPageQueryParam = PlayerPageRequestFactoryBot.builder().negativePage().build();
        Function<UriBuilder, URI> invalidPage = uriBuilder -> uriBuilder
                .queryParam("page", invalidPageQueryParam.page())
                .queryParam("limit", invalidPageQueryParam.limit())
                .queryParam("sentence", invalidPageQueryParam.sentence())
                .queryParam("sortBy", invalidPageQueryParam.sortBy())
                .queryParam("sortDirection", invalidPageQueryParam.sortDirection())
                .build();
        var lessZeroLimitQueryParam = PlayerPageRequestFactoryBot.builder().lessThanZeroLimit().build();
        Function<UriBuilder, URI> lessZeroLimit = uriBuilder -> uriBuilder
                .queryParam("page", lessZeroLimitQueryParam.page())
                .queryParam("limit", lessZeroLimitQueryParam.limit())
                .queryParam("sentence", lessZeroLimitQueryParam.sentence())
                .queryParam("sortBy", lessZeroLimitQueryParam.sortBy())
                .queryParam("sortDirection", lessZeroLimitQueryParam.sortDirection())
                .build();
        var greaterFiftyQueryParam = PlayerPageRequestFactoryBot.builder().greaterThanFiftyLimit().build();
        Function<UriBuilder, URI> greaterFifty = uriBuilder -> uriBuilder
                .queryParam("page", greaterFiftyQueryParam.page())
                .queryParam("limit", greaterFiftyQueryParam.limit())
                .queryParam("sentence", greaterFiftyQueryParam.sentence())
                .queryParam("sortBy", greaterFiftyQueryParam.sortBy())
                .queryParam("sortDirection", greaterFiftyQueryParam.sortDirection())
                .build();
        return Stream.of(
                Arguments.of(invalidPage, "page"),
                Arguments.of(lessZeroLimit, "limit"),
                Arguments.of(greaterFifty, "limit")
        );
    }

    @ParameterizedTest
    @MethodSource
    void checkConstraintsTest(final Function<UriBuilder, URI> uriFunction, final String field){
        problemResponseDTORequestBuilder.uri(uriFunction)
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsBadRequest()
                .assertBody(actual ->{
                    assertThat(actual).isNotNull();
                    assertThat(actual.status()).isEqualTo(BAD_REQUEST.value());
                    assertThat(actual.fields().stream().map(ErrorFieldResponseDTO::name).toList()).contains(field);
                });
    }
}
