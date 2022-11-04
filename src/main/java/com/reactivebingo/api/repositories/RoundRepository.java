package com.reactivebingo.api.repositories;

import com.reactivebingo.api.documents.RoundDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoundRepository extends ReactiveMongoRepository<RoundDocument, String> {
}

