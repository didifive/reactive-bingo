package com.reactivebingo.api.documents;

import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;

public record DrawnNumber(Short number,
                          @CreatedDate
                          @Field("drawn_at")
                          OffsetDateTime drawnAt) {

    @Builder(toBuilder = true)
    public DrawnNumber {
    }

}