package com.reactivebingo.api.controllers.docs;

import com.reactivebingo.api.configs.mongo.validation.MongoId;
import com.reactivebingo.api.dtos.PlayerRequestDTO;
import com.reactivebingo.api.dtos.PlayerResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Tag(name = "Jogador", description = "Endpoints para Jogadores")
public interface PlayerControllerDocs {

    String PLAYER_ID_DESCRIPTION = "Identificador do jogador";
    String PLAYER_ID_EXAMPLE = "63668c0459dc8d40ac62a1e1";
    String MEDIA_TYPE_APPLICATION_JSON = "application/json";

    @Operation(summary = "Endpoint para cadastrar novo jogador")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "retorna o jogador criado",
                    content = {@Content(mediaType = MEDIA_TYPE_APPLICATION_JSON
                            , schema = @Schema(implementation = PlayerResponseDTO.class))})
    })
    Mono<PlayerResponseDTO> save(@Valid @RequestBody PlayerRequestDTO request);

    @Operation(summary = "Endpoint para atualizar um jogador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "o jogador foi atualizado",
                    content = {@Content(mediaType = MEDIA_TYPE_APPLICATION_JSON
                            , schema = @Schema(implementation = PlayerResponseDTO.class))})
    })
    Mono<PlayerResponseDTO> update(@Parameter(description = PLAYER_ID_DESCRIPTION, example = PLAYER_ID_EXAMPLE)
                                   @PathVariable @Valid @MongoId(message = "{playerController.id}") String id
            , @Valid @RequestBody PlayerRequestDTO request);

    @Operation(summary = "Endpoint para excluir um jogador")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "o jogador foi excluído")
    })
    Mono<Void> delete(@Parameter(description = PLAYER_ID_DESCRIPTION, example = PLAYER_ID_EXAMPLE)
                      @PathVariable @Valid @MongoId(message = "{playerController.id}") String id);

    @Operation(summary = "Endpoint para pesquisar um jogador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "o jogador encontrado e retornado",
                    content = {@Content(mediaType = MEDIA_TYPE_APPLICATION_JSON
                            , schema = @Schema(implementation = PlayerResponseDTO.class))}),
            @ApiResponse(responseCode = "404", description = "o jogador NÃO foi encontrado", content = {})
    })
    Mono<PlayerResponseDTO> findBy(@Parameter(description = PLAYER_ID_DESCRIPTION, example = PLAYER_ID_EXAMPLE)
                                   @PathVariable @Valid @MongoId(message = "{playerController.id}") String id);

    @Operation(summary = "Endpoint para buscar todos os jogadores")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "retornar os jogadores cadastrados",
                    content = {@Content(mediaType = MEDIA_TYPE_APPLICATION_JSON,
                            array = @ArraySchema(schema = @Schema(implementation = PlayerResponseDTO.class)))})
    })
    Flux<PlayerResponseDTO> findAll();
}
