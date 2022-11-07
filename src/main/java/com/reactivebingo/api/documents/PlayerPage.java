package com.reactivebingo.api.documents;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

public record PlayerPage(Long currentPage,
                         Long totalPages,
                         Long totalItems,
                         List<PlayerDocument> content) {

    public static PlayerPageBuilder builder() {
        return new PlayerPageBuilder();
    }

    public PlayerPageBuilder toBuilder(final Integer limit) {
        return new PlayerPageBuilder(limit, currentPage, totalItems, content);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlayerPageBuilder {
        private Integer limit;
        private Long currentPage;
        private Long totalItems;
        private List<PlayerDocument> content;

        public PlayerPageBuilder limit(final Integer limit) {
            this.limit = limit;
            return this;
        }

        public PlayerPageBuilder currentPage(final Long currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public PlayerPageBuilder totalItems(final Long totalItems) {
            this.totalItems = totalItems;
            return this;
        }

        public PlayerPageBuilder content(final List<PlayerDocument> content) {
            this.content = content;
            return this;
        }

        public PlayerPage build() {
            var totalPages = (totalItems / limit) + ((totalItems % limit > 0) ? 1 : 0);
            return new PlayerPage(currentPage, totalPages, totalItems, content);
        }

    }

}
