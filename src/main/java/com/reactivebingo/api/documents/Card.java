package com.reactivebingo.api.documents;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

public record Card(@Field("player_id")
                   String playerId,
                   Set<Short> numbers,
                   @Field("checked_numbers")
                   Set<Short> checkedNumbers,
                   @CreatedDate
                   @Field("created_at")
                   OffsetDateTime createdAt) {

    public static CardBuilder builder() {
        return new CardBuilder();
    }

    public CardBuilder toBuilder() {
        return new CardBuilder(playerId, numbers, checkedNumbers, createdAt);
    }

    @NoArgsConstructor
    @AllArgsConstructor
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

        public Card build() {
            return new Card(playerId, numbers, checkedNumbers, createdAt);
        }
    }

}
