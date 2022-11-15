package com.reactivebingo.api.utils.request;

import com.reactivebingo.api.dtos.responses.PageResponseDTO;
import com.reactivebingo.api.dtos.responses.PlayerResponseDTO;
import com.reactivebingo.api.dtos.responses.ProblemResponseDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class RequestBuilder<B> {

    public static RequestBuilder<Void> noContentRequestBuilder(final ApplicationContext applicationContext,
                                                               final String baseUri){
        return new RequestBuilder<>(applicationContext, baseUri, Void.class);
    }

    public static RequestBuilder<ProblemResponseDTO> problemResponseDTORequestBuilder(final ApplicationContext applicationContext,
                                                                                      final String baseUri){
        return new RequestBuilder<>(applicationContext, baseUri, ProblemResponseDTO.class);
    }

    public static RequestBuilder<PlayerResponseDTO> playerResponseDTORequestBuilder(final ApplicationContext applicationContext,
                                                                                 final String baseUri) {
        return new RequestBuilder<>(applicationContext, baseUri, PlayerResponseDTO.class);
    }

    public static RequestBuilder<PageResponseDTO> playerPageResponseDTORequestBuilder(final ApplicationContext applicationContext,
                                                                                      final String baseUri) {
        return new RequestBuilder<>(applicationContext, baseUri, PageResponseDTO.class);
    }

//    public static RequestBuilder<DeckResponse> deckResponseRequestBuilder(final ApplicationContext applicationContext,
//                                                                       final String baseUri){
//        return new RequestBuilder<>(applicationContext, baseUri, DeckResponse.class);
//    }

//    public static RequestBuilder<QuestionResponse> questionResponseRequestBuilder(final ApplicationContext applicationContext,
//                                                                                  final String baseUri){
//        return new RequestBuilder<>(applicationContext, baseUri, QuestionResponse.class);
//    }
//
//    public static RequestBuilder<AnswerQuestionResponse> answerQuestionResponseRequestBuilder(final ApplicationContext applicationContext,
//                                                                                        final String baseUri){
//        return new RequestBuilder<>(applicationContext, baseUri, AnswerQuestionResponse.class);
//    }

    private final WebTestClient webTestClient;
    private Function<UriBuilder, URI> uriFunction;
    private final Map<String, Set<String>> headers = new HashMap<>();
    private Object body;
    private final Class<B> responseClass;

    public RequestBuilder(final ApplicationContext context, final String baseUri, final Class<B> responseClass){
        this.responseClass = responseClass;
        this.webTestClient = WebTestClient
                .bindToApplicationContext(context)
                .configureClient()
                .baseUrl(baseUri)
                .responseTimeout(Duration.ofDays(1))
                .build();
    }

    public RequestBuilder<B> uri(final Function<UriBuilder, URI> uriFunction){
        this.uriFunction = uriFunction;
        return this;
    }

    public RequestBuilder<B> body(final Object body){
        this.body = body;
        return this;
    }

    public RequestBuilder<B> header(final String key, final Set<String> value){
        this.headers.put(key, value);
        return this;
    }

    public SimpleRequestBuilder<B> generateRequestWithSimpleBody(){
        if (Objects.isNull(uriFunction)){
            throw new IllegalArgumentException("Informe a uri do recurso a ser consumido");
        }
        return new SimpleRequestBuilder<>(webTestClient, uriFunction, headers, body, responseClass);
    }

    public NoBodyRequestBuilder generateRequestWithoutBody(){
        if (Objects.isNull(uriFunction)){
            throw new IllegalArgumentException("Informe a uri do recurso a ser consumido");
        }
        if (responseClass != Void.class){
            throw new IllegalArgumentException("Use a clessa Void para requisições sem response body");
        }
        return new NoBodyRequestBuilder(webTestClient, uriFunction, headers, body);
    }

    public CollectionRequestBuilder<B> generateRequestWithCollectionBody(){
        if (Objects.isNull(uriFunction)){
            throw new IllegalArgumentException("Informe a uri do recurso a ser consumido");
        }
        if (responseClass == Void.class){
            throw new IllegalArgumentException("Use a classe Void para requisições sem response body");
        }
        return new CollectionRequestBuilder<>(webTestClient, uriFunction, headers, body, responseClass);
    }

}
