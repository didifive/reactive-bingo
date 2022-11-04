package com.reactivebingo.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record RoundResponseDTO(@JsonProperty("id")
                               String id,
                               @JsonProperty("name")
                               String name,
                               @JsonProperty("drawnNumbers")
                               Set<DrawnNumberDTO> drawnNumbers,
                               @JsonProperty("cards")
                               Set<CardDTO> cards,
                               @JsonProperty("complete")
                               Boolean complete,
                               @JsonProperty("createdAt")
                               OffsetDateTime createdAt,
                               @JsonProperty("updatedAt")
                               OffsetDateTime updatedAt) {


    public static RoundDocumentBuilder builder() {
        return new RoundDocumentBuilder();
    }

    public RoundDocumentBuilder toBuilder() {
        return new RoundDocumentBuilder(id, name, drawnNumbers, cards, createdAt, updatedAt);
    }

    public Boolean isStarted() {
        return !drawnNumbers.isEmpty();
    }

    public Boolean hasPlayer(String playerId) {
        return isStarted() &&
                cards.stream()
                        .anyMatch(card -> card.playerId().equals(playerId));
    }

    public DrawnNumberDTO getLastDrawnNumber() {
        return drawnNumbers.stream()
                .max(Comparator.comparing(DrawnNumberDTO::drawnAt))
                .orElseThrow();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoundDocumentBuilder {

        private String id;
        private String name;
        private Set<DrawnNumberDTO> drawnNumbers = new HashSet<>();
        private Set<CardDTO> cards = new HashSet<>();
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public RoundDocumentBuilder id(final String id) {
            this.id = id;
            return this;
        }

        public RoundDocumentBuilder name(final String name) {
            this.name = name;
            return this;
        }


        public RoundDocumentBuilder drawnNumbers(final Set<DrawnNumberDTO> drawnNumbers) {
            this.drawnNumbers = drawnNumbers;
            return this;
        }

        public RoundDocumentBuilder cards(final Set<CardDTO> cards) {
            this.cards = cards;
            return this;
        }

        public RoundDocumentBuilder createdAt(final OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public RoundDocumentBuilder updatedAt(final OffsetDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public RoundResponseDTO build() {
            var complete = cards.stream()
                    .anyMatch(card ->
                            drawnNumbers.stream()
                                    .map(DrawnNumberDTO::number)
                                    .collect(Collectors.toSet())
                                    .containsAll(card.numbers()));
            return new RoundResponseDTO(id, name, drawnNumbers, cards, complete, createdAt, updatedAt);
        }
    }
}
