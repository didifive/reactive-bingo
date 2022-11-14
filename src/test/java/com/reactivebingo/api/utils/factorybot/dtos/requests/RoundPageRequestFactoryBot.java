package com.reactivebingo.api.utils.factorybot.dtos.requests;

import com.github.javafaker.Faker;
import com.reactivebingo.api.dtos.enums.RoundSortBy;
import com.reactivebingo.api.dtos.enums.SortDirection;
import com.reactivebingo.api.dtos.requests.RoundPageRequestDTO;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static com.reactivebingo.api.utils.factorybot.RandomData.randomEnum;

public class RoundPageRequestFactoryBot {

    public static RoundPageRequestFactoryBotBuilder builder() {
        return new RoundPageRequestFactoryBotBuilder();
    }

    public static class RoundPageRequestFactoryBotBuilder {

        private final String sentence;
        private final RoundSortBy sortBy;
        private final SortDirection sortDirection;
        private final Faker faker = getFaker();
        private Long page;
        private Integer limit;

        public RoundPageRequestFactoryBotBuilder() {
            this.sentence = faker.lorem().sentence();
            this.page = faker.number().numberBetween(0L, 3L);
            this.limit = faker.number().numberBetween(20, 40);
            this.sortBy = randomEnum(RoundSortBy.class);
            this.sortDirection = randomEnum(SortDirection.class);
        }

        public RoundPageRequestFactoryBotBuilder negativePage() {
            this.page = faker.number().numberBetween(Long.MIN_VALUE, 0);
            return this;
        }

        public RoundPageRequestFactoryBotBuilder lessThanZeroLimit() {
            this.limit = faker.number().numberBetween(Integer.MIN_VALUE, 1);
            return this;
        }

        public RoundPageRequestFactoryBotBuilder greaterThanFiftyLimit() {
            this.limit = faker.number().numberBetween(51, Integer.MAX_VALUE);
            return this;
        }

        public RoundPageRequestDTO build() {
            return RoundPageRequestDTO.builder()
                    .sentence(sentence)
                    .page(page)
                    .limit(limit)
                    .sortBy(sortBy)
                    .sortDirection(sortDirection)
                    .build();
        }

    }

}
