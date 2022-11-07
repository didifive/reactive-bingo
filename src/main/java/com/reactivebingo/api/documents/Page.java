package com.reactivebingo.api.documents;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

public record Page(Long currentPage,
                   Long totalPages,
                   Long totalItems,
                   List<?> content) {

    public static PageBuilder builder() {
        return new PageBuilder();
    }

    public PageBuilder toBuilder(final Integer limit) {
        return new PageBuilder(limit, currentPage, totalItems, content);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class PageBuilder {
        private Integer limit;
        private Long currentPage;
        private Long totalItems;
        private List<?> content;

        public PageBuilder limit(final Integer limit) {
            this.limit = limit;
            return this;
        }

        public PageBuilder currentPage(final Long currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public PageBuilder totalItems(final Long totalItems) {
            this.totalItems = totalItems;
            return this;
        }

        public PageBuilder content(final List<?> content) {
            this.content = content;
            return this;
        }

        public Page build() {
            var totalPages = (totalItems / limit) + ((totalItems % limit > 0) ? 1 : 0);
            return new Page(currentPage, totalPages, totalItems, content);
        }

    }

}
