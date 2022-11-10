package com.reactivebingo.api.mappers;

import com.reactivebingo.api.documents.Card;
import com.reactivebingo.api.documents.RoundDocument;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface CardDomainMapper {
    default Card toCardWithNumber(Card card, Set<Short> numbers){
        card.numbers().addAll(numbers);
        return card;
    };

    default RoundDocument addCardToDocument(Card card, RoundDocument document){
        document.cards().add(card);
        return document;
    };

}
