package com.reactivebingo.api.utils.factorybot.dtos;

import com.reactivebingo.api.dtos.DrawnNumberDTO;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class DrawnNumberDTOFactoryBot {

    public static DrawnNumberDTOFactoryBotBuilder builder() {
        return new DrawnNumberDTOFactoryBotBuilder();
    }

    public static class DrawnNumberDTOFactoryBotBuilder {

        private final Short number;
        private final OffsetDateTime drawnAt;

        public DrawnNumberDTOFactoryBotBuilder() {
            var faker = getFaker();
            this.number = Short.parseShort(String.valueOf(faker.number().numberBetween(0, 99)));
            this.drawnAt = OffsetDateTime.now();
        }

        public DrawnNumberDTO build() {
            return DrawnNumberDTO.builder()
                    .number(number)
                    .drawnAt(drawnAt)
                    .build();
        }

    }

}
