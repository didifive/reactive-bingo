package com.reactivebingo.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record PlayerRequestDTO(@JsonProperty("name")
                               @NotBlank
                               @Size(min = 1, max = 255)
                               String name,
                               @NotBlank
                               @Size(min = 1, max = 255)
                               @Email
                               @JsonProperty("email")
                               String email) {

    @Builder(toBuilder = true)
    public PlayerRequestDTO {
    }
}
