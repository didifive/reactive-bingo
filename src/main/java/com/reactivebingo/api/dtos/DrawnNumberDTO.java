package com.reactivebingo.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.OffsetDateTime;

public record DrawnNumberDTO(@JsonProperty("number")
                             @Schema(description = "número sorteado"
                                     , example = "10"
                                     , type = "integer")
                             Short number,
                             @JsonProperty("drawnAt")
                             @Schema(description = "data em que o número foi sorteado"
                                     , example = "2022-11-05T19:40:35.0680489Z"
                                     , type = "string"
                                     , format = "date-time")
                             OffsetDateTime drawnAt) {

    @Builder(toBuilder = true)
    public DrawnNumberDTO {
    }
}
