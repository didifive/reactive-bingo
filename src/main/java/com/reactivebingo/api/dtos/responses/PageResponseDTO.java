package com.reactivebingo.api.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

public record PageResponseDTO(@JsonProperty("currentPage")
                              @Schema(description = "pagina retornada", example = "1")
                              Long currentPage,
                              @JsonProperty("totalPages")
                              @Schema(description = "total de páginas", example = "20")
                              Long totalPages,
                              @JsonProperty("totalItems")
                              @Schema(description = "soma ", example = "total de itens existentes")
                              Long totalItems,
                              @JsonProperty("content")
                              @Schema(description = "itens da página")
                              List<?> content) {

    public static PlayerPageResponseDTOBuilder builder() {
        return new PlayerPageResponseDTOBuilder();
    }

    public PlayerPageResponseDTOBuilder toBuilder(final Integer limit) {
        return new PlayerPageResponseDTOBuilder(limit, currentPage, totalItems, content);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlayerPageResponseDTOBuilder {
        private Integer limit;
        private Long currentPage;
        private Long totalItems;
        private List<?> content;

        public PlayerPageResponseDTOBuilder limit(final Integer limit) {
            this.limit = limit;
            return this;
        }

        public PlayerPageResponseDTOBuilder currentPage(final Long currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public PlayerPageResponseDTOBuilder totalItems(final Long totalItems) {
            this.totalItems = totalItems;
            return this;
        }

        public PlayerPageResponseDTOBuilder content(final List<?> content) {
            this.content = content;
            return this;
        }

        public PageResponseDTO build() {
            var totalPages = (totalItems / limit) + ((totalItems % limit > 0) ? 1 : 0);
            return new PageResponseDTO(currentPage, totalPages, totalItems, content);
        }

    }

}
