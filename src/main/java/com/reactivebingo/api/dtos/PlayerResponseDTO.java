package com.reactivebingo.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.OffsetDateTime;

public record PlayerResponseDTO(@JsonProperty("id")
                                String id,
                                @JsonProperty("name")
                                String name,
                                @JsonProperty("email")
                                String email,
                                @JsonProperty("createdAt")
                                OffsetDateTime createdAt,
                                @JsonProperty("updatedAt")
                                OffsetDateTime updatedAt) {

    @Builder(toBuilder = true)
    public PlayerResponseDTO {
    }

}
