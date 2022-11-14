package com.reactivebingo.api.utils.factorybot.dtos.responses;

import com.github.javafaker.Faker;
import com.reactivebingo.api.documents.RoundDocument;
import com.reactivebingo.api.dtos.responses.PageResponseDTO;
import com.reactivebingo.api.utils.factorybot.documents.RoundDocumentFactoryBot;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class RoundPageResponseFactoryBot {

    public static RoundPageReponseFactoryBotBuilder builder() {
        return new RoundPageReponseFactoryBotBuilder();
    }

    public static class RoundPageReponseFactoryBotBuilder {
        private final Faker faker = getFaker();
        private Long currentPage;
        private Integer limit;
        private Long totalItems;
        private List<RoundDocument> content;

        public RoundPageReponseFactoryBotBuilder() {
            this.currentPage = faker.number().numberBetween(1L, 20L);
            this.limit = faker.number().numberBetween(1, 10);
            var rounds = Stream.generate(() -> RoundDocumentFactoryBot.builder().build())
                    .limit(limit)
                    .toList();
            this.content = rounds;
            this.totalItems = faker.number().numberBetween(rounds.size(), rounds.size() * 3L);
        }

        public RoundPageReponseFactoryBotBuilder emptyPage() {
            this.currentPage = 0L;
            this.limit = faker.number().numberBetween(5, 15);
            this.totalItems = 0L;
            this.content = new ArrayList<>();
            return this;
        }

        public PageResponseDTO build() {
            return PageResponseDTO.builder()
                    .currentPage(currentPage)
                    .totalItems(totalItems)
                    .content(content)
                    .limit(limit)
                    .build();
        }

    }

}
