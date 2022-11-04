package com.reactivebingo.api.documents;

import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.Set;

public record Card(@Field("player_id")
                   String playerId,
                   Set<Short> numbers,
                   @CreatedDate
                   @Field("created_at")
                   OffsetDateTime createdAt) {

    @Builder(toBuilder = true)
    public Card {}

}
