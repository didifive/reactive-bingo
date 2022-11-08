package com.reactivebingo.api.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.OffsetDateTime;

public record PlayerResponseDTO(@JsonProperty("id")
                                @Schema(description = "id do jogador"
                                        , example = "63668c0459dc8d40ac62a1e1"
                                        , type = "string")
                                String id,
                                @JsonProperty("name")
                                @Schema(description = "nome do jogador"
                                        , example = "João da Silva"
                                        , type = "string")
                                String name,
                                @JsonProperty("email")
                                @Schema(description = "e-mail do jogador"
                                        , example = "joao@email.com"
                                        , type = "string")
                                String email,
                                @JsonProperty("createdAt")
                                @Schema(description = "data de criação do jogador"
                                        , example = "2022-11-05T19:40:35.0680489Z"
                                        , type = "string"
                                        , format = "date-time")
                                OffsetDateTime createdAt,
                                @JsonProperty("updatedAt")
                                @Schema(description = "data de atualização do jogador"
                                        , example = "2022-11-05T19:40:35.0680489Z"
                                        , type = "string"
                                        , format = "date-time")
                                OffsetDateTime updatedAt) {

    @Builder(toBuilder = true)
    public PlayerResponseDTO {
    }

}
