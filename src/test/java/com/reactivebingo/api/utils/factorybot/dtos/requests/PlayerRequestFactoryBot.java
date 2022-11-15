package com.reactivebingo.api.utils.factorybot.dtos.requests;

import com.github.javafaker.Faker;
import com.reactivebingo.api.dtos.requests.PlayerRequestDTO;
import lombok.NoArgsConstructor;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class PlayerRequestFactoryBot {

    public static PlayerRequestFactoryBotBuilder builder() {
        return new PlayerRequestFactoryBotBuilder();
    }

    public static class PlayerRequestFactoryBotBuilder {

        private final Faker faker = getFaker();
        private String name;
        private String email;

        public PlayerRequestFactoryBotBuilder() {
            this.name = faker.name().name();
            this.email = faker.internet().emailAddress();
        }

        public PlayerRequestFactoryBotBuilder blankName() {
            this.name = faker.bool().bool() ? null : " ";
            return this;
        }

        public PlayerRequestFactoryBotBuilder longName() {
            this.name = faker.lorem().sentence(256);
            return this;
        }

        public PlayerRequestFactoryBotBuilder blankEmail() {
            this.email = faker.bool().bool() ? null : " ";
            return this;
        }

        public PlayerRequestFactoryBotBuilder longEmail() {
            this.email = faker.lorem().sentence(256);
            return this;
        }

        public PlayerRequestFactoryBotBuilder invalidEmail() {
            this.email = faker.lorem().word();
            return this;
        }

        public PlayerRequestDTO build() {
            return PlayerRequestDTO.builder()
                    .name(name)
                    .email(email)
                    .build();
        }

    }

}
