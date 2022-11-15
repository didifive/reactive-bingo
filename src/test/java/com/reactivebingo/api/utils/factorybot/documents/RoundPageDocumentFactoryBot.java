package com.reactivebingo.api.utils.factorybot.documents;

import com.github.javafaker.Faker;
import com.reactivebingo.api.documents.Page;
import com.reactivebingo.api.documents.RoundDocument;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class RoundPageDocumentFactoryBot {

    public static RoundPageDocumentFactoryBotBuilder builder() {
        return new RoundPageDocumentFactoryBotBuilder();
    }

    public static class RoundPageDocumentFactoryBotBuilder {
        private final Faker faker = getFaker();
        private Long currentPage;
        private Integer limit;
        private Long totalItems;
        private List<RoundDocument> content;

        public RoundPageDocumentFactoryBotBuilder() {
            this.currentPage = faker.number().numberBetween(1L, 20L);
            this.limit = faker.number().numberBetween(1, 10);
            var rounds = Stream.generate(() -> RoundDocumentFactoryBot.builder().build())
                    .limit(limit)
                    .toList();
            this.content = rounds;
            this.totalItems = faker.number().numberBetween(rounds.size(), rounds.size() * 3L);
        }

        public RoundPageDocumentFactoryBotBuilder emptyPage() {
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
