package com.reactivebingo.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.OffsetDateTime;

public record DrawnNumberDTO(@JsonProperty("number")
                             Short number,
                             @JsonProperty("drawnAt")
                             OffsetDateTime drawnAt) {

    @Builder(toBuilder = true)
    public DrawnNumberDTO {
    }
}
