package com.reactivebingo.api.utils.factorybot.documents;

import com.github.javafaker.Faker;
import com.reactivebingo.api.documents.Page;
import com.reactivebingo.api.documents.PlayerDocument;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class PlayerPageDocumentFactoryBot {

    public static PlayerPageDocumentFactoryBotBuilder builder() {
        return new PlayerPageDocumentFactoryBotBuilder();
    }

    public static class PlayerPageDocumentFactoryBotBuilder {
        private final Faker faker = getFaker();
        private Long currentPage;
        private Integer limit;
        private Long totalItems;
        private List<PlayerDocument> content;

        public PlayerPageDocumentFactoryBotBuilder() {
            this.currentPage = faker.number().numberBetween(1L, 20L);
            this.limit = faker.number().numberBetween(1, 10);
            var users = Stream.generate(() -> PlayerDocumentFactoryBot.builder().build())
                    .limit(limit)
                    .toList();
            this.content = users;
            this.totalItems = faker.number().numberBetween(users.size(), users.size() * 3L);
        }

        public PlayerPageDocumentFactoryBotBuilder emptyPage() {
            this.currentPage = 0L;
            this.limit = faker.number().numberBetween(5, 15);
            this.totalItems = 0L;
            this.content = new ArrayList<>();
            return this;
        }

        public Page build() {
            return Page.builder()
                    .currentPage(currentPage)
                    .totalItems(totalItems)
                    .content(content)
                    .limit(limit)
                    .build();
        }

    }

}
