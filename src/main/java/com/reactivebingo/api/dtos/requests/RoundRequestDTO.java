package com.reactivebingo.api.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record RoundRequestDTO(@JsonProperty("name")
                              @NotBlank
                              @Size(min = 1, max = 255)
                              @Schema(description = "nome da rodada"
                                      , example = "Bingo do carrão"
                                      , type = "string")
                              String name,
                              @JsonProperty("prize")
                              @NotBlank
                              @Size(min = 1, max = 255)
                              @Schema(description = "prêmio da rodada"
                                      , example = "Fusca 86"
                                      , type = "string")
                              String prize) {

    @Builder(toBuilder = true)
    public RoundRequestDTO {
    }

}
