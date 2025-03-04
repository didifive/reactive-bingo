package com.reactivebingo.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

public record CardDTO(@JsonProperty("playerId")
                      @Schema(description = "id do jogador"
                              , example = "63668c0459dc8d40ac62a1e1"
                              , type = "string")
                      String playerId,
                      @JsonProperty("numbers")
                      @ArraySchema(schema = @Schema(description = "número da cartela"
                              , example = "10"
                              , type = "integer"))
                      Set<Short> numbers,
                      @JsonProperty("checkedNumbers")
                      @ArraySchema(schema = @Schema(description = "número acertado"
                              , example = "10"
                              , type = "integer"))
                      Set<Short> checkedNumbers,
                      @JsonProperty("complete")
                      @Schema(description = "cartela completada"
                              , example = "false"
                              , type = "boolean")
                      Boolean complete,
                      @JsonProperty("createdAt")
                      @Schema(description = "data de criação da cartela"
                              , example = "2022-11-05T19:40:35.0680489Z"
                              , type = "string"
                              , format = "date-time")
                      OffsetDateTime createdAt) {

    public static CardBuilder builder() {
        return new CardBuilder();
    }

    public CardBuilder toBuilder() {
        return new CardBuilder(playerId, numbers, checkedNumbers, createdAt);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class CardBuilder {
        private String playerId;
        private Set<Short> numbers = new HashSet<>();
        private Set<Short> checkedNumbers = new HashSet<>();
        private OffsetDateTime createdAt;

        public CardBuilder playerId(final String playerId) {
            this.playerId = playerId;
            return this;
        }

        public CardBuilder numbers(final Set<Short> numbers) {
            this.numbers = numbers;
            return this;
        }

        public CardBuilder checkedNumbers(final Set<Short> checkedNumbers) {
            this.checkedNumbers = checkedNumbers;
            return this;
        }

        public CardBuilder createdAt(final OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CardDTO build() {
            var complete = (numbers.size() == 20
                    && numbers.size() == checkedNumbers.size());
            return new CardDTO(playerId, numbers, checkedNumbers, complete, createdAt);
        }
    }
}
