package com.reactivebingo.api.utils.factorybot.dtos.responses;

import com.reactivebingo.api.documents.Card;
import com.reactivebingo.api.documents.DrawnNumber;
import com.reactivebingo.api.documents.RoundDocument;
import com.reactivebingo.api.dtos.CardDTO;
import com.reactivebingo.api.dtos.DrawnNumberDTO;
import com.reactivebingo.api.dtos.responses.RoundResponseDTO;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.reactivebingo.api.services.RoundService.MAX_DECKS_PER_ROUND;
import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class RoundResponseDTOFactoryBot {

    public static RoundResponseDTOFactoryBotBuilder builder() {
        return new RoundResponseDTOFactoryBotBuilder();
    }

    public static class RoundResponseDTOFactoryBotBuilder {

        private final String name;
        private final String prize;
        private final Set<DrawnNumberDTO> drawnNumbers;
        private final Set<CardDTO> cards;
        private String id;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public RoundResponseDTOFactoryBotBuilder() {
            var faker = getFaker();
            this.id = ObjectId.get().toString();
            this.name = faker.lorem().characters(1, 255);
            this.prize = faker.lorem().characters(1, 255);
            this.drawnNumbers = new HashSet<>();
            this.cards = new HashSet<>();
            this.createdAt = OffsetDateTime.now();
            this.updatedAt = OffsetDateTime.now();
        }

        public RoundResponseDTOFactoryBotBuilder withDrawnNumbers(Integer amount) {
            this.drawnNumbers.clear();
            while (this.drawnNumbers.size() <= amount) {
                var randomNumber = Short.parseShort(String.valueOf(new Random().nextInt(99)));
                if (this.drawnNumbers.stream().noneMatch(drawnNumber ->
                        drawnNumber.number().equals(randomNumber))) {
                    var drawNumber = DrawnNumberDTO.builder()
                            .drawnAt(OffsetDateTime.now())
                            .number(randomNumber)
                            .build();
                    this.drawnNumbers.add(drawNumber);
                }
            }
            return this;
        }

        public RoundResponseDTOFactoryBotBuilder withCard(CardDTO card) {
            this.cards.clear();
            this.cards.add(card);
            return this;
        }

        public RoundResponseDTO build() {
            return RoundResponseDTO.builder()
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
