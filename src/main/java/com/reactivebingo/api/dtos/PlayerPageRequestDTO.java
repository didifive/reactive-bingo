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
                                   String sentence,
                                   @PositiveOrZero
                                   @JsonProperty("page")
                                   Long page,
                                   @Min(1)
                                   @Max(50)
                                   @JsonProperty("limit")
                                   Integer limit,
                                   @JsonProperty("sortBy")
                                   PlayerSortBy sortBy,
                                   @JsonProperty("sortDirection")
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
        return page * limit;
    }

}
