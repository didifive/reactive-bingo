package com.reactivebingo.api.utils.factorybot.dtos;

import com.reactivebingo.api.dtos.CardDTO;
import com.reactivebingo.api.utils.factorybot.dtos.responses.RoundResponseDTOFactoryBot;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CardDTOFactoryBot {

    public static CardDTOFactoryBotBuilder builder() {
        return new CardDTOFactoryBotBuilder();
    }

    public static class CardDTOFactoryBotBuilder {

        private String playerId;
        private final Set<Short> numbers;
        private final Set<Short> checkedNumbers;
        private final OffsetDateTime createdAt;

        public CardDTOFactoryBotBuilder() {
            this.playerId = ObjectId.get().toString();
            this.numbers = getRandomNumbersSet(20);
            this.checkedNumbers = new HashSet<>(numbers);
            this.createdAt = OffsetDateTime.now();
        }

        private Set<Short> getRandomNumbersSet(int limit) {
            Set<Short> randomNumbers = new HashSet<>();
            while (randomNumbers.size() < limit) {
                randomNumbers.add(Short.parseShort(String.valueOf(getFaker().number().numberBetween(0, 99))));
            }
            return randomNumbers;
        }

        public CardDTOFactoryBotBuilder withPlayerId(String playerId) {
            this.playerId = playerId;
            return this;
        }

        public CardDTO build() {
            return CardDTO.builder()
                    .playerId(playerId)
                    .numbers(numbers)
                    .checkedNumbers(checkedNumbers)
                    .createdAt(createdAt)
                    .build();
        }

    }

}
