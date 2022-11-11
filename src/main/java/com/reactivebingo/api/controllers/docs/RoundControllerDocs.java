package com.reactivebingo.api.controllers.docs;

import com.reactivebingo.api.configs.mongo.validation.MongoId;
import com.reactivebingo.api.dtos.CardDTO;
import com.reactivebingo.api.dtos.DrawnNumberDTO;
import com.reactivebingo.api.dtos.enums.RoundSortBy;
import com.reactivebingo.api.dtos.enums.SortDirection;
import com.reactivebingo.api.dtos.requests.RoundPageRequestDTO;
import com.reactivebingo.api.dtos.requests.RoundRequestDTO;
import com.reactivebingo.api.dtos.responses.PageResponseDTO;
import com.reactivebingo.api.dtos.responses.PlayerResponseDTO;
import com.reactivebingo.api.dtos.responses.ProblemResponseDTO;
import com.reactivebingo.api.dtos.responses.RoundResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static com.reactivebingo.api.controllers.docs.PlayerControllerDocs.PLAYER_ID_DESCRIPTION;
import static com.reactivebingo.api.controllers.docs.PlayerControllerDocs.PLAYER_ID_EXAMPLE;

@Tag(name = "Rodada", description = "Endpoints para Rodadas")
public interface RoundControllerDocs {

    String ROUND_ID_DESCRIPTION = "Identificador da Rodada";
    String ROUND_ID_EXAMPLE = "63668c0459dc8d40ac62a1e1";

    String MEDIA_TYPE_APPLICATION_JSON = "application/json";

    @Operation(summary = "Endpoint para cadastrar nova rodada")
    @ApiResponse(responseCode = "201", description = "retorna a rodada criada",
            content = {@Content(mediaType = MEDIA_TYPE_APPLICATION_JSON
                    , schema = @Schema(implementation = RoundResponseDTO.class))})
    Mono<RoundResponseDTO> save(@Valid @RequestBody final RoundRequestDTO request);

    @Operation(summary = "Endpoint para sortear número para a rodada")
    @ApiResponse(responseCode = "201", description = "retorna o número sorteado",
            content = {@Content(mediaType = MEDIA_TYPE_APPLICATION_JSON
                    , schema = @Schema(implementation = DrawnNumberDTO.class))})
    @ApiResponse(responseCode = "404", description = "a rodada não foi encontrada"
            , content = {@Content(mediaType = MEDIA_TYPE_APPLICATION_JSON
            , schema = @Schema(implementation = ProblemResponseDTO.class))})
    Mono<DrawnNumberDTO> drawNumber(@Parameter(description = ROUND_ID_DESCRIPTION, example = ROUND_ID_EXAMPLE)
                                    @PathVariable @Valid @MongoId(message = "{roundController.id}") final String id);

    @Operation(summary = "Endpoint para pesquisar último número sorteado na rodada")
    @ApiResponse(responseCode = "200", description = "retorna o último número sorteado na rodada"
            , content = {@Content(mediaType = MEDIA_TYPE_APPLICATION_JSON
            , schema = @Schema(implementation = DrawnNumberDTO.class))})
    @ApiResponse(responseCode = "404", description = "a rodada não foi encontrada"
            , content = {@Content(mediaType = MEDIA_TYPE_APPLICATION_JSON
            , schema = @Schema(implementation = ProblemResponseDTO.class))})
    Mono<DrawnNumberDTO> getLastNumber(@Parameter(description = ROUND_ID_DESCRIPTION, example = ROUND_ID_EXAMPLE)
                                       @PathVariable @Valid @MongoId(message = "{roundController.id}") final String id);

    @Operation(summary = "Endpoint para gerar nova cartela na rodada para o jogador especificado")
    @ApiResponse(responseCode = "201", description = "retorna a cartela gerada",
            content = {@Content(mediaType = MEDIA_TYPE_APPLICATION_JSON
                    , schema = @Schema(implementation = CardDTO.class))})
    @ApiResponse(responseCode = "404", description = "a rodada ou jogador não foi encontrada(o)"
            , content = {@Content(mediaType = MEDIA_TYPE_APPLICATION_JSON
            , schema = @Schema(implementation = ProblemResponseDTO.class))})
    Mono<CardDTO> generateCard(@Parameter(description = ROUND_ID_DESCRIPTION, example = ROUND_ID_EXAMPLE)
                               @PathVariable @Valid @MongoId(message = "{roundController.id}") String id
            , @Parameter(description = PLAYER_ID_DESCRIPTION, example = PLAYER_ID_EXAMPLE)
                               @PathVariable @Valid @MongoId(message = "{playerController.id}") String playerId);

    @Operation(summary = "Endpoint para pesquisar cartela da rodada")
    @ApiResponse(responseCode = "200", description = "retorna cartela da rodada"
            , content = {@Content(mediaType = MEDIA_TYPE_APPLICATION_JSON
            , schema = @Schema(implementation = CardDTO.class))})
    @ApiResponse(responseCode = "404", description = "a rodada ou cartela não foi encontrada"
            , content = {@Content(mediaType = MEDIA_TYPE_APPLICATION_JSON
            , schema = @Schema(implementation = ProblemResponseDTO.class))})
    Mono<CardDTO> findCardByPlayerId(@Parameter(description = ROUND_ID_DESCRIPTION, example = ROUND_ID_EXAMPLE)
                                     @PathVariable @Valid @MongoId(message = "{roundController.id}") String id
            , @Parameter(description = PLAYER_ID_DESCRIPTION, example = ROUND_ID_EXAMPLE)
                                     @PathVariable @Valid @MongoId(message = "{playerController.id}") String playerId);

    @Operation(summary = "Endpoint para pesquisar uma rodada")
    @ApiResponse(responseCode = "200", description = "rodada retornada"
            , content = {@Content(mediaType = MEDIA_TYPE_APPLICATION_JSON
            , schema = @Schema(implementation = RoundResponseDTO.class))})
    @ApiResponse(responseCode = "404", description = "a rodada não foi encontrada"
            , content = {@Content(mediaType = MEDIA_TYPE_APPLICATION_JSON
            , schema = @Schema(implementation = ProblemResponseDTO.class))})
    Mono<RoundResponseDTO> findById(@Parameter(description = ROUND_ID_DESCRIPTION, example = ROUND_ID_EXAMPLE)
                                    @PathVariable @Valid @MongoId(message = "{roundController.id}") final String id);


    @Operation(summary = "Endpoint para buscar todas as rodadas com filtro de paginação")
    @ApiResponse(responseCode = "200", description = "retornar os jogadores cadastrados conforme filtro",
            content = {@Content(mediaType = MEDIA_TYPE_APPLICATION_JSON
                    , schema = @Schema(implementation = PageResponseDTO.class))})
    @Parameter(in = ParameterIn.QUERY
            , schema = @Schema(type = "string")
            , name = "sentence"
            , description = "texto para filtrar por nome ou premio (case insensitive)"
            , example = "liquidificador")
    @Parameter(in = ParameterIn.QUERY
            , schema = @Schema(type = "integer", format = "int64", defaultValue = "0")
            , name = "page"
            , description = "página solicitada"
            , example = "1")
    @Parameter(in = ParameterIn.QUERY
            , schema = @Schema(type = "integer", format = "int32", minimum = "1", maximum = "50", defaultValue = "20")
            , name = "limit"
            , description = "tamanho da página"
            , example = "30")
    @Parameter(in = ParameterIn.QUERY
            , schema = @Schema(implementation = RoundSortBy.class)
            , name = "sortBy"
            , description = "campo para ordenação - padrão: NAME"
            , example = "NAME")
    @Parameter(in = ParameterIn.QUERY
            , schema = @Schema(implementation = SortDirection.class)
            , name = "sortDirection"
            , description = "sentido da ordenação - padrão: ASC"
            , example = "ASC")
    Mono<PageResponseDTO> findAll(@Parameter(hidden = true) @Valid RoundPageRequestDTO request);

}
