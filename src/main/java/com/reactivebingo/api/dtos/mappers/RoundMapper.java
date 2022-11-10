package com.reactivebingo.api.dtos.mappers;

import com.reactivebingo.api.documents.Page;
import com.reactivebingo.api.documents.RoundDocument;
import com.reactivebingo.api.dtos.responses.PageResponseDTO;
import com.reactivebingo.api.dtos.requests.RoundRequestDTO;
import com.reactivebingo.api.dtos.responses.RoundResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

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
    RoundResponseDTO toPageResponse(final RoundDocument document);

    @Named("roundResponseDTOList")
    default List<RoundResponseDTO> toRoundResponseDTOList(List<?> source) {
        return source
                .stream()
                .map(s -> toPageResponse((RoundDocument) s))
                .collect(Collectors.toList());
    }

    @Mapping(target = "content", qualifiedByName = "roundResponseDTOList")
    PageResponseDTO toPageResponse(final Page page, final Integer limit);

}

