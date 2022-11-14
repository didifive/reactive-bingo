package com.reactivebingo.api.utils.factorybot.dtos.responses;

import com.github.javafaker.Faker;
import com.reactivebingo.api.documents.PlayerDocument;
import com.reactivebingo.api.dtos.responses.PageResponseDTO;
import com.reactivebingo.api.utils.factorybot.documents.PlayerDocumentFactoryBot;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class PlayerPageResponseFactoryBot {

    public static PlayerPageReponseFactoryBotBuilder builder() {
        return new PlayerPageReponseFactoryBotBuilder();
    }

    public static class PlayerPageReponseFactoryBotBuilder {
        private final Faker faker = getFaker();
        private Long currentPage;
        private Integer limit;
        private Long totalItems;
        private List<PlayerDocument> content;

        public PlayerPageReponseFactoryBotBuilder() {
            this.currentPage = faker.number().numberBetween(1L, 20L);
            this.limit = faker.number().numberBetween(1, 10);
            var players = Stream.generate(() -> PlayerDocumentFactoryBot.builder().build())
                    .limit(limit)
                    .toList();
            this.content = players;
            this.totalItems = faker.number().numberBetween(players.size(), players.size() * 3L);
        }

        public PlayerPageReponseFactoryBotBuilder emptyPage() {
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
