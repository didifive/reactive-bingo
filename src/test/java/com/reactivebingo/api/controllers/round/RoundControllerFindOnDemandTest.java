package com.reactivebingo.api.controllers.round;

import com.reactivebingo.api.controllers.AbstractControllerTest;
import com.reactivebingo.api.controllers.RoundController;
import com.reactivebingo.api.documents.Page;
import com.reactivebingo.api.dtos.mappers.CardMapperImpl;
import com.reactivebingo.api.dtos.mappers.DrawnNumberMapperImpl;
import com.reactivebingo.api.dtos.mappers.RoundMapperImpl;
import com.reactivebingo.api.dtos.requests.RoundPageRequestDTO;
import com.reactivebingo.api.dtos.responses.ErrorFieldResponseDTO;
import com.reactivebingo.api.dtos.responses.PageResponseDTO;
import com.reactivebingo.api.dtos.responses.ProblemResponseDTO;
import com.reactivebingo.api.services.RoundService;
import com.reactivebingo.api.services.queries.RoundQueryService;
import com.reactivebingo.api.utils.factorybot.documents.RoundPageDocumentFactoryBot;
import com.reactivebingo.api.utils.factorybot.dtos.requests.RoundPageRequestFactoryBot;
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

import static com.reactivebingo.api.utils.request.RequestBuilder.pageResponseDTORequestBuilder;
import static com.reactivebingo.api.utils.request.RequestBuilder.problemResponseDTORequestBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ContextConfiguration(classes = {RoundMapperImpl.class
        , CardMapperImpl.class
        , DrawnNumberMapperImpl.class})
@WebFluxTest(RoundController.class)
class RoundControllerFindOnDemandTest extends AbstractControllerTest {

    @MockBean
    private RoundService roundService;
    @MockBean
    private RoundQueryService roundQueryService;
    private RequestBuilder<PageResponseDTO> pageResponseDTORequestBuilder;
    private RequestBuilder<ProblemResponseDTO> problemResponseDTORequestBuilder;

    @BeforeEach
    void setup(){
        pageResponseDTORequestBuilder = pageResponseDTORequestBuilder(applicationContext, "/rounds");
        problemResponseDTORequestBuilder = problemResponseDTORequestBuilder(applicationContext, "/rounds");
    }

    private static Stream<Page> findOnDemandTest(){
        return Stream.of(
                RoundPageDocumentFactoryBot.builder().build(),
                RoundPageDocumentFactoryBot.builder().emptyPage().build()
        );
    }

    @ParameterizedTest
    @MethodSource
    void findOnDemandTest(final Page pageDocument){
        var queryParams = RoundPageRequestFactoryBot.builder().build();
        when(roundQueryService.findOnDemand(any(RoundPageRequestDTO.class))).thenReturn(Mono.just(pageDocument));
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
        var invalidPageQueryParam = RoundPageRequestFactoryBot.builder().negativePage().build();
        Function<UriBuilder, URI> invalidPage = uriBuilder -> uriBuilder
                .queryParam("page", invalidPageQueryParam.page())
                .queryParam("limit", invalidPageQueryParam.limit())
                .queryParam("sentence", invalidPageQueryParam.sentence())
                .queryParam("sortBy", invalidPageQueryParam.sortBy())
                .queryParam("sortDirection", invalidPageQueryParam.sortDirection())
                .build();
        var lessZeroLimitQueryParam = RoundPageRequestFactoryBot.builder().lessThanZeroLimit().build();
        Function<UriBuilder, URI> lessZeroLimit = uriBuilder -> uriBuilder
                .queryParam("page", lessZeroLimitQueryParam.page())
                .queryParam("limit", lessZeroLimitQueryParam.limit())
                .queryParam("sentence", lessZeroLimitQueryParam.sentence())
                .queryParam("sortBy", lessZeroLimitQueryParam.sortBy())
                .queryParam("sortDirection", lessZeroLimitQueryParam.sortDirection())
                .build();
        var greaterFiftyQueryParam = RoundPageRequestFactoryBot.builder().greaterThanFiftyLimit().build();
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
