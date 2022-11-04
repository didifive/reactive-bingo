package com.reactivebingo.api.dtos.mappers;

import com.reactivebingo.api.documents.Card;
import com.reactivebingo.api.dtos.CardDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CardMapper {

    Card toDocument(final CardDTO dto);

    CardDTO toDto(final Card document);

    @Named("cardDTOSet")
    default Set<CardDTO> toCardDTOSet(Set<Card> source) {
        return source
                .stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }

}
