package com.reactivebingo.api.dtos.mappers;

import com.reactivebingo.api.documents.RoundDocument;
import com.reactivebingo.api.dtos.RoundRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring"
        , uses = {CardMapper.class, DrawnNumberMapper.class})
public interface RoundMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "drawnNumbers", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RoundDocument toDocument(final RoundRequestDTO request);

    @Mapping(target = "drawnNumbers", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RoundDocument toDocument(final RoundRequestDTO request, final String id);


    @Mapping(target = "drawnNumbers", source = "drawnNumbers", qualifiedByName = "drawnNumberDTOSet")
    @Mapping(target = "cards", source = "cards", qualifiedByName = "cardDTOSet")
    RoundRequestDTO toResponse(final RoundDocument document);

}

