package com.reactivebingo.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record RoundRequestDTO(@JsonProperty("name")
                              @NotBlank
                              @Size(min = 1, max = 255)
                              String name,
                              @JsonProperty("prize")
                              @NotBlank
                              @Size(min = 1, max = 255)
                              String prize) {

    @Builder(toBuilder = true)
    public RoundRequestDTO {
    }

}
