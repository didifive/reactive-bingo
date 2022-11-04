package com.reactivebingo.api.documents;

import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.Set;

@Document(collection = "players")
public record PlayerDocument(@Id
                             String id,
                             String name,
                             String email,
                             @CreatedDate
                             @Field("created_at")
                             OffsetDateTime createdAt,
                             @LastModifiedDate
                             @Field("updated_at")
                             OffsetDateTime updatedAt) {

    @Builder(toBuilder = true)
    public PlayerDocument {}

}
