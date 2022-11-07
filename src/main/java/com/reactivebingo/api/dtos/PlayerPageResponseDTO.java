package com.reactivebingo.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

public record PlayerPageResponseDTO(@JsonProperty("currentPage")
                               @Schema(description = "pagina retornada", example = "1")
                               Long currentPage,
                                    @JsonProperty("totalPages")
                               @Schema(description = "total de páginas", example = "20")
                               Long totalPages,
                                    @JsonProperty("totalItems")
                               @Schema(description = "soma ", example = "quantidade de registros paginados")
                               Long totalItems,
                                    @JsonProperty("content")
                               @Schema(description = "jogadores da página")
                               List<PlayerResponseDTO> content) {

    public static PlayerPageResponseDTOBuilder builder(){
        return new PlayerPageResponseDTOBuilder();
    }

    public PlayerPageResponseDTOBuilder toBuilder(final Integer limit){
        return new PlayerPageResponseDTOBuilder(limit, currentPage, totalItems, content);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlayerPageResponseDTOBuilder{
        private Integer limit;
        private Long currentPage;
        private Long totalItems;
        private List<PlayerResponseDTO> content;

        public PlayerPageResponseDTOBuilder limit(final Integer limit){
            this.limit = limit;
            return this;
        }

        public PlayerPageResponseDTOBuilder currentPage(final Long currentPage){
            this.currentPage = currentPage;
            return this;
        }

        public PlayerPageResponseDTOBuilder totalItems(final Long totalItems){
            this.totalItems = totalItems;
            return this;
        }

        public PlayerPageResponseDTOBuilder content(final List<PlayerResponseDTO> content){
            this.content = content;
            return this;
        }

        public PlayerPageResponseDTO build(){
            var totalPages = (totalItems / limit) + ((totalItems % limit > 0) ? 1 : 0);
            return new PlayerPageResponseDTO(currentPage, totalPages, totalItems, content);
        }

    }

}
