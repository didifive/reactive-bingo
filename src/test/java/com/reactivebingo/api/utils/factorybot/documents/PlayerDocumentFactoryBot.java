package com.reactivebingo.api.utils.factorybot.documents;

import com.reactivebingo.api.documents.PlayerDocument;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.OffsetDateTime;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class PlayerDocumentFactoryBot {

    public static PlayerDocumentFactoryBotBuilder builder() {
        return new PlayerDocumentFactoryBotBuilder();
    }

    public static class PlayerDocumentFactoryBotBuilder {

        private String id;
        private final String name;
        private final String email;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public PlayerDocumentFactoryBotBuilder() {
            var faker = getFaker();
            this.id = ObjectId.get().toString();
            this.name = faker.name().name();
            this.email = faker.internet().emailAddress();
            this.createdAt = OffsetDateTime.now();
            this.updatedAt = OffsetDateTime.now();
        }

        public PlayerDocumentFactoryBotBuilder preInsert() {
            this.id = null;
            this.createdAt = null;
            this.updatedAt = null;
            return this;
        }

        public PlayerDocumentFactoryBotBuilder preUpdate(final String id) {
            this.id = id;
            return this;
        }

        public PlayerDocument build() {
            return PlayerDocument.builder()
                    .id(id)
                    .name(name)
                    .email(email)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();
        }

    }

}
