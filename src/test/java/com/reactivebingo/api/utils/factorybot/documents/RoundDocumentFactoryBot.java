package com.reactivebingo.api.utils.factorybot.documents;

import com.reactivebingo.api.documents.Card;
import com.reactivebingo.api.documents.DrawnNumber;
import com.reactivebingo.api.documents.RoundDocument;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static java.time.ZoneOffset.UTC;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class RoundDocumentFactoryBot {

    public static RoundDocumentFactoryBotBuilder builder() {
        return new RoundDocumentFactoryBotBuilder();
    }

    public static class RoundDocumentFactoryBotBuilder {

        private final String name;
        private final String prize;
        private String id;
        private final Set<DrawnNumber> drawnNumbers;
        private final Set<Card> cards;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public RoundDocumentFactoryBotBuilder() {
            var faker = getFaker();
            this.id = ObjectId.get().toString();
            this.name = faker.name().name();
            this.prize = faker.lorem().characters(1, 255);
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

        public RoundDocumentFactoryBotBuilder withDrawnNumbers(Integer amount) {
            this.drawnNumbers.clear();
            while (this.drawnNumbers.size() <= amount) {
                var randomNumber = Short.parseShort(String.valueOf(new Random().nextInt(99)));
                if (this.drawnNumbers.stream().noneMatch(drawnNumber ->
                        drawnNumber.number().equals(randomNumber))) {
                    var drawNumber = DrawnNumber.builder()
                            .drawnAt(OffsetDateTime.now(UTC))
                            .number(randomNumber)
                            .build();
                    this.drawnNumbers.add(drawNumber);
                }
            }
            return this;
        }

        public RoundDocumentFactoryBotBuilder withCardToPlayer(String playerId) {
            this.cards.clear();
            this.cards.add(Card.builder()
                    .playerId(playerId)
                    .build());
            return this;
        }

        public RoundDocumentFactoryBotBuilder withCompletedCard() {
            var cardNumbers = this.drawnNumbers.stream()
                    .map(DrawnNumber::number)
                    .collect(Collectors.toSet())
                    .stream().limit(20)
                    .collect(Collectors.toSet());
            this.cards.clear();
            this.cards.add(Card.builder()
                    .numbers(cardNumbers)
                    .checkedNumbers(cardNumbers)
                    .build());
            return this;
        }

        public RoundDocumentFactoryBotBuilder withIncompletedCard() {
            var cardNumbers = this.drawnNumbers.stream()
                    .map(DrawnNumber::number)
                    .collect(Collectors.toSet())
                    .stream().limit(20)
                    .collect(Collectors.toSet());
            this.cards.clear();
            this.cards.add(Card.builder()
                    .numbers(cardNumbers)
                    .checkedNumbers(new HashSet<>(cardNumbers.stream()
                            .toList().subList(0, 18)))
                    .build());
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
