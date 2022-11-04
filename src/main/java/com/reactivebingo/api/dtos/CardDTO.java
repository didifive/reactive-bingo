package com.reactivebingo.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

public record CardDTO(@JsonProperty("playerId")
                      String playerId,
                      @JsonProperty("numbers")
                      Set<Short> numbers,
                      @JsonProperty("createdAt")
                      OffsetDateTime createdAt) {

    public static CardBuilder builder() {
        return new CardBuilder();
    }

    public CardBuilder toBuilder() {
        return new CardBuilder(playerId, numbers, createdAt);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class CardBuilder {
        private String playerId;
        private Set<Short> numbers = new HashSet<>();
        private OffsetDateTime createdAt;

        public CardBuilder playerId(final String playerId) {
            this.playerId = playerId;
            return this;
        }

        public CardBuilder numbers(final Set<Short> numbers) {
            this.numbers = numbers;
            return this;
        }

        public CardBuilder createdAt(final OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CardDTO build() {
            return new CardDTO(playerId, numbers, createdAt);
        }
    }
}
