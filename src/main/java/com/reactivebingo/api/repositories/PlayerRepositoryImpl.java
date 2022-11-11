package com.reactivebingo.api.repositories;

import com.reactivebingo.api.documents.PlayerDocument;
import com.reactivebingo.api.dtos.requests.PlayerPageRequestDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@Slf4j
@AllArgsConstructor
public class PlayerRepositoryImpl {

    private ReactiveMongoTemplate template;


    public Flux<PlayerDocument> findOnDemand(final PlayerPageRequestDTO request) {
        return Mono.just(new Query())
                .flatMap(query -> buildWhere(query, request.sentence()))
                .map(query -> query.with(request.getSort()).skip(request.getSkip()).limit(request.limit()))
                .doFirst(() -> log.info("==== Find players on demand with follow request {}", request))
                .flatMapMany(query -> template.find(query, PlayerDocument.class));
    }

    public Mono<Long> count(final PlayerPageRequestDTO request) {
        return Mono.just(new Query())
                .flatMap(query -> buildWhere(query, request.sentence()))
                .doFirst(() -> log.info("==== Counting players with follow request {}", request))
                .flatMap(query -> template.count(query, PlayerDocument.class));
    }

    private Mono<Query> buildWhere(final Query query, final String sentence) {
        return Mono.just(query)
                .filter(q -> StringUtils.isBlank(sentence))
                .switchIfEmpty(Mono.defer(() -> Mono.just(query))
                        .flatMapIterable(q -> List.of("name", "email"))
                        .map(dbField -> where(dbField).regex(sentence, "i"))
                        .collectList()
                        .map(setWhereClause(query)));
    }

    private Function<List<Criteria>, Query> setWhereClause(final Query query) {
        return c -> {
            var whereClause = new Criteria();
            whereClause.orOperator(c);
            return query.addCriteria(whereClause);
        };
    }

}
