package com.reactivebingo.api.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record PlayerRequestDTO(@JsonProperty("name")
                               @NotBlank
                               @Size(min = 1, max = 255)
                               @Schema(description = "nome do jogador"
                                       , example = "João da Silva"
                                       , type = "string")
                               String name,
                               @NotBlank
                               @Size(min = 1, max = 255)
                               @Email
                               @JsonProperty("email")
                               @Schema(description = "e-mail do jogador"
                                       , example = "joao@email.com"
                                       , type = "string")
                               String email) {

    @Builder(toBuilder = true)
    public PlayerRequestDTO {
    }
}
