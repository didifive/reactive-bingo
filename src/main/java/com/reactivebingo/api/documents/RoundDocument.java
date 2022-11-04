package com.reactivebingo.api.documents;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "rounds")
public record RoundDocument(@Id
                            String id,
                            String name,
                            Set<DrawnNumber> drawnNumbers,
                            Set<Card> cards,
                            @CreatedDate
                            @Field("created_at")
                            OffsetDateTime createdAt,
                            @LastModifiedDate
                            @Field("updated_at")
                            OffsetDateTime updatedAt) {

    public static RoundDocumentBuilder builder() {
        return new RoundDocumentBuilder();
    }

    public RoundDocumentBuilder toBuilder() {
        return new RoundDocumentBuilder(id, name, drawnNumbers, cards, createdAt, updatedAt);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoundDocumentBuilder {

        private String id;
        private String name;
        private Set<DrawnNumber> drawnNumbers = new HashSet<>();
        private Set<Card> cards = new HashSet<>();
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


        public RoundDocumentBuilder drawnNumbers(final Set<DrawnNumber> drawnNumbers) {
            this.drawnNumbers = drawnNumbers;
            return this;
        }

        public RoundDocumentBuilder cards(final Set<Card> cards) {
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

        public RoundDocument build() {
            return new RoundDocument(id, name, drawnNumbers, cards, createdAt, updatedAt);
        }
    }

}