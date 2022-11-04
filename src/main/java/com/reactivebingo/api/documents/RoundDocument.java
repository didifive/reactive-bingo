package com.reactivebingo.api.documents;

import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Document(collection = "rounds")
public record RoundDocument(@Id
                            String id,
                            String name,
                            Set<DrawnNumber> drawnNumbers,
                            Set<Card> cards,
                            @CreatedDate
                            @Field("created_at")
                            OffsetDateTime createdAt,
                            @LastModifiedDate
                            @Field("updated_at")
                            OffsetDateTime updatedAt) {

    public Boolean isStarted(){
        return !drawnNumbers.isEmpty();
    }

    public Boolean hasWinner(){
        return isStarted() &&
                cards.stream()
                        .anyMatch(card ->
                                drawnNumbers.stream()
                                        .map(DrawnNumber::number)
                                        .collect(Collectors.toSet())
                                        .containsAll(card.numbers()));
    }

    public Boolean hasPlayer(String playerId){
        return isStarted() &&
                cards.stream()
                        .anyMatch(card -> card.playerId().equals(playerId));
    }

    @Builder(toBuilder = true)
    public RoundDocument {}

}