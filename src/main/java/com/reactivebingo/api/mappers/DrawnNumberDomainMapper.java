package com.reactivebingo.api.mappers;

import com.reactivebingo.api.documents.DrawnNumber;
import com.reactivebingo.api.documents.RoundDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DrawnNumberDomainMapper {
    default RoundDocument addDrawnNumberToRound(RoundDocument document, DrawnNumber drawnNumber){
        document.drawnNumbers().add(drawnNumber);
        return document;
    };

}
