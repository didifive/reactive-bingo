package com.reactivebingo.api.controllers.docs;

import com.reactivebingo.api.dtos.PlayerRequestDTO;
import com.reactivebingo.api.dtos.PlayerResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Jogador", description = "Endpoints para Jogadores")
public interface PlayerControllerDocs {

    @Operation(summary = "Endpoint para cadastrar novo jogador")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "retorna o jogador criado",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PlayerResponseDTO.class))})
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    Mono<PlayerResponseDTO> save(@Valid @RequestBody PlayerRequestDTO request);
}
