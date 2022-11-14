package com.reactivebingo.api.utils.factorybot.dtos.requests;

import com.github.javafaker.Faker;
import com.reactivebingo.api.dtos.enums.PlayerSortBy;
import com.reactivebingo.api.dtos.enums.SortDirection;
import com.reactivebingo.api.dtos.requests.PlayerPageRequestDTO;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static com.reactivebingo.api.utils.factorybot.RandomData.randomEnum;

public class PlayerPageRequestFactoryBot {

    public static PlayerPageRequestFactoryBotBuilder builder() {
        return new PlayerPageRequestFactoryBotBuilder();
    }

    public static class PlayerPageRequestFactoryBotBuilder {

        private final String sentence;
        private final PlayerSortBy sortBy;
        private final SortDirection sortDirection;
        private final Faker faker = getFaker();
        private Long page;
        private Integer limit;

        public PlayerPageRequestFactoryBotBuilder() {
            this.sentence = faker.lorem().sentence();
            this.page = faker.number().numberBetween(0L, 3L);
            this.limit = faker.number().numberBetween(20, 40);
            this.sortBy = randomEnum(PlayerSortBy.class);
            this.sortDirection = randomEnum(SortDirection.class);
        }

        public PlayerPageRequestFactoryBotBuilder negativePage() {
            this.page = faker.number().numberBetween(Long.MIN_VALUE, 0);
            return this;
        }

        public PlayerPageRequestFactoryBotBuilder lessThanZeroLimit() {
            this.limit = faker.number().numberBetween(Integer.MIN_VALUE, 1);
            return this;
        }

        public PlayerPageRequestFactoryBotBuilder greaterThanFiftyLimit() {
            this.limit = faker.number().numberBetween(51, Integer.MAX_VALUE);
            return this;
        }

        public PlayerPageRequestDTO build() {
            return PlayerPageRequestDTO.builder()
                    .sentence(sentence)
                    .page(page)
                    .limit(limit)
                    .sortBy(sortBy)
                    .sortDirection(sortDirection)
                    .build();
        }

    }

}
