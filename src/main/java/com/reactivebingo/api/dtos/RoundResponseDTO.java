package com.reactivebingo.api.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record RoundResponseDTO(@JsonProperty("id")
                               @Schema(description = "id da rodada"
                                       , example = "63668c0459dc8d40ac62a1e1"
                                       , type = "string")
                               String id,
                               @JsonProperty("name")
                               @Schema(description = "nome da rodada"
                                       , example = "Bingo do carrão"
                                       , type = "string")
                               String name,
                               @JsonProperty("prize")
                               @Schema(description = "prêmio da rodada"
                                       , example = "Fusca 86"
                                       , type = "string")
                               String prize,
                               @JsonProperty("drawnNumbers")
                               @Schema(description = "números sorteados"
                                       , implementation = DrawnNumberDTO.class)
                               Set<DrawnNumberDTO> drawnNumbers,
                               @JsonProperty("cards")
                               @Schema(description = "cartelas da rodada"
                                       , implementation = CardDTO.class)
                               Set<CardDTO> cards,
                               @JsonProperty("complete")
                               @Schema(description = "rodada completada"
                                       , example = "false"
                                       , type = "boolean")
                               Boolean complete,
                               @JsonProperty("createdAt")
                               @Schema(description = "data de criação da rodada"
                                       , example = "2022-11-05T19:40:35.0680489Z"
                                       , type = "string"
                                       , format = "date-time")
                               OffsetDateTime createdAt,
                               @JsonProperty("updatedAt")
                               @Schema(description = "data de atualização da rodada"
                                       , example = "2022-11-05T19:40:35.0680489Z"
                                       , type = "string"
                                       , format = "date-time")
                               OffsetDateTime updatedAt) {


    public static RoundDocumentBuilder builder() {
        return new RoundDocumentBuilder();
    }

    public RoundDocumentBuilder toBuilder() {
        return new RoundDocumentBuilder(id, name, prize, drawnNumbers, cards, createdAt, updatedAt);
    }

    @JsonIgnore(value = true)
    public Boolean isStarted() {
        return !drawnNumbers.isEmpty();
    }

    @JsonIgnore(value = true)
    public Boolean hasPlayer(String playerId) {
        return isStarted() &&
                cards.stream()
                        .anyMatch(card -> card.playerId().equals(playerId));
    }

    @JsonIgnore(value = true)
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
        private String prize;
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

        public RoundDocumentBuilder prize(final String prize) {
            this.prize = prize;
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
                    .anyMatch(card -> card.complete());
            return new RoundResponseDTO(id, name, prize, drawnNumbers, cards, complete, createdAt, updatedAt);
        }
    }
}
