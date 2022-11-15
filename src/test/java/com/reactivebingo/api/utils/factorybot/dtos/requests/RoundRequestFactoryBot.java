package com.reactivebingo.api.utils.factorybot.dtos.requests;

import com.github.javafaker.Faker;
import com.reactivebingo.api.dtos.requests.RoundRequestDTO;
import lombok.NoArgsConstructor;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class RoundRequestFactoryBot {

    public static RoundRequestFactoryBotBuilder builder() {
        return new RoundRequestFactoryBotBuilder();
    }

    public static class RoundRequestFactoryBotBuilder {

        private final Faker faker = getFaker();
        private String name;
        private String prize;

        public RoundRequestFactoryBotBuilder() {
            this.name = faker.animal().name();
            this.prize = faker.chuckNorris().fact();
        }

        public RoundRequestFactoryBotBuilder blankName() {
            this.name = faker.bool().bool() ? null : " ";
            return this;
        }

        public RoundRequestFactoryBotBuilder longName() {
            this.name = faker.lorem().sentence(256);
            return this;
        }

        public RoundRequestFactoryBotBuilder blankPrize() {
            this.prize = faker.bool().bool() ? null : " ";
            return this;
        }

        public RoundRequestFactoryBotBuilder longPrize() {
            this.prize = faker.lorem().sentence(256);
            return this;
        }

        public RoundRequestDTO build() {
            return RoundRequestDTO.builder()
                    .name(name)
                    .prize(prize)
                    .build();
        }

    }

}
