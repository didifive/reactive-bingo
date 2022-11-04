package com.reactivebingo.api.dtos.mappers;

import com.reactivebingo.api.documents.Card;
import com.reactivebingo.api.documents.DrawnNumber;
import com.reactivebingo.api.dtos.CardDTO;
import com.reactivebingo.api.dtos.DrawnNumberDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DrawnNumberMapper {

    DrawnNumber toDocument(final DrawnNumberDTO dto);

    DrawnNumberDTO toDto(final DrawnNumber document);

    @Named("drawnNumberDTOSet")
    default Set<DrawnNumberDTO> toDrawnNumberDTOSet(Set<DrawnNumber> source) {
        return source
                .stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }

}
