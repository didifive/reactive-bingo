package com.reactivebingo.api.documents;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

public record PlayerPageDocument(Long currentPage,
                                 Long totalPages,
                                 Long totalItems,
                                 List<PlayerDocument> content) {

    public static UserPageDocumentBuilder builder() {
        return new UserPageDocumentBuilder();
    }

    public UserPageDocumentBuilder toBuilder(final Integer limit) {
        return new UserPageDocumentBuilder(limit, currentPage, totalItems, content);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserPageDocumentBuilder {
        private Integer limit;
        private Long currentPage;
        private Long totalItems;
        private List<PlayerDocument> content;

        public UserPageDocumentBuilder limit(final Integer limit) {
            this.limit = limit;
            return this;
        }

        public UserPageDocumentBuilder currentPage(final Long currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public UserPageDocumentBuilder totalItems(final Long totalItems) {
            this.totalItems = totalItems;
            return this;
        }

        public UserPageDocumentBuilder content(final List<PlayerDocument> content) {
            this.content = content;
            return this;
        }

        public PlayerPageDocument build() {
            var totalPages = (totalItems / limit) + ((totalItems % limit > 0) ? 1 : 0);
            return new PlayerPageDocument(currentPage, totalPages, totalItems, content);
        }

    }

}
