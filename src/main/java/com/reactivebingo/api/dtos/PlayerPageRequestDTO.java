package com.reactivebingo.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.reactivebingo.api.dtos.enums.PlayerSortBy;
import com.reactivebingo.api.dtos.enums.SortDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

import static com.reactivebingo.api.dtos.enums.PlayerSortBy.NAME;
import static com.reactivebingo.api.dtos.enums.SortDirection.ASC;
import static com.reactivebingo.api.dtos.enums.SortDirection.DESC;

public record PlayerPageRequestDTO(@JsonProperty("sentence")
                                   @Schema(description = "texto para filtrar por nome e email (case insensitive)", example = "ana")
                                   String sentence,
                                   @PositiveOrZero
                                   @JsonProperty("page")
                                   @Schema(description = "página solicitada", example = "1", defaultValue = "0")
                                   Long page,
                                   @Min(1)
                                   @Max(50)
                                   @JsonProperty("limit")
                                   @Schema(description = "tamanho da página", example = "30", defaultValue = "20")
                                   Integer limit,
                                   @JsonProperty("sortBy")
                                   @Schema(description = "campo para ordenação", enumAsRef = true, defaultValue = "NAME")
                                   PlayerSortBy sortBy,
                                   @JsonProperty("sortDirection")
                                   @Schema(description = "sentido da ordenação", enumAsRef = true, defaultValue = "ASC")
                                   SortDirection sortDirection) {

    @Builder(toBuilder = true)
    public PlayerPageRequestDTO {
        sortBy = ObjectUtils.defaultIfNull(sortBy, NAME);
        sortDirection = ObjectUtils.defaultIfNull(sortDirection, ASC);
        limit = ObjectUtils.defaultIfNull(limit, 20);
        page = ObjectUtils.defaultIfNull(page, 0L);
    }

    @Schema(hidden = true)
    public Sort getSort() {
        return sortDirection.equals(DESC) ? Sort.by(sortBy.getField()).descending() : Sort.by(sortBy.getField()).ascending();
    }

    @Schema(hidden = true)
    public Long getSkip() {
        return page > 0 ? ((page - 1) * limit) : 0;
    }

}
