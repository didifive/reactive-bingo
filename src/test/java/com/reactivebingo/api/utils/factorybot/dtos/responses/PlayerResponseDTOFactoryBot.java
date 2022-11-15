package com.reactivebingo.api.utils.factorybot.dtos.responses;

import com.reactivebingo.api.documents.PlayerDocument;
import com.reactivebingo.api.dtos.responses.PlayerResponseDTO;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.OffsetDateTime;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class PlayerResponseDTOFactoryBot {

    public static PlayerResponseDTOFactoryBotBuilder builder() {
        return new PlayerResponseDTOFactoryBotBuilder();
    }

    public static class PlayerResponseDTOFactoryBotBuilder {

        private String id;
        private final String name;
        private final String email;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public PlayerResponseDTOFactoryBotBuilder() {
            var faker = getFaker();
            this.id = ObjectId.get().toString();
            this.name = faker.name().name();
            this.email = faker.internet().emailAddress();
            this.createdAt = OffsetDateTime.now();
            this.updatedAt = OffsetDateTime.now();
        }

        public PlayerResponseDTO build() {
            return PlayerResponseDTO.builder()
                    .id(id)
                    .name(name)
                    .email(email)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();
        }

    }

}
