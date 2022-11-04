package com.reactivebingo.api.repositories;

import com.reactivebingo.api.documents.PlayerDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PlayerRepository extends ReactiveMongoRepository<PlayerDocument, String> {

    Mono<PlayerDocument> findByEmail(final String email);

}

