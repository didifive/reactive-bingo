package com.reactivebingo.api.dtos.mappers;

import com.reactivebingo.api.documents.Page;
import com.reactivebingo.api.documents.PlayerDocument;
import com.reactivebingo.api.dtos.requests.PlayerRequestDTO;
import com.reactivebingo.api.dtos.responses.PageResponseDTO;
import com.reactivebingo.api.dtos.responses.PlayerResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PlayerDocument toDocument(final PlayerRequestDTO request);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PlayerDocument toDocument(final PlayerRequestDTO request, final String id);

    @Named("playerToResponse")
    PlayerResponseDTO toResponse(final PlayerDocument document);

    @Named("playerResponseDTOList")
    default List<PlayerResponseDTO> toPlayerResponseDTOList(List<?> source) {
        return source
                .stream()
                .map(s -> toResponse((PlayerDocument) s))
                .toList();
    }

    @Mapping(target = "content", qualifiedByName = "playerResponseDTOList")
    PageResponseDTO toResponse(final Page page, final Integer limit);

}
