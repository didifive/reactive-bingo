package com.reactivebingo.api.utils.factorybot.documents;

import com.reactivebingo.api.documents.Card;
import com.reactivebingo.api.documents.DrawnNumber;
import com.reactivebingo.api.documents.RoundDocument;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class RoundDocumentFactoryBot {

    public static RoundDocumentFactoryBotBuilder builder() {
        return new RoundDocumentFactoryBotBuilder();
    }

    public static class RoundDocumentFactoryBotBuilder {

        private String id;
        private final String name;
        private final String prize;
        private Set<DrawnNumber> drawnNumbers = new HashSet<>();
        private Set<Card> cards = new HashSet<>();
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public RoundDocumentFactoryBotBuilder() {
            var faker = getFaker();
            this.id = ObjectId.get().toString();
            this.name = faker.name().name();
            this.prize = faker.lorem().characters(1,255);
            this.drawnNumbers = new HashSet<>();
            this.cards = new HashSet<>();
            this.createdAt = OffsetDateTime.now();
            this.updatedAt = OffsetDateTime.now();
        }

        public RoundDocumentFactoryBotBuilder preInsert() {
            this.id = null;
            this.createdAt = null;
            this.updatedAt = null;
            return this;
        }

        public RoundDocumentFactoryBotBuilder preUpdate(final String id) {
            this.id = id;
            return this;
        }

        public RoundDocument build() {
            return RoundDocument.builder()
                    .id(id)
                    .name(name)
                    .prize(prize)
                    .drawnNumbers(drawnNumbers)
                    .cards(cards)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();
        }

    }

}
