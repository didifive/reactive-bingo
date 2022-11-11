package com.reactivebingo.api.dtos.mappers;

import com.reactivebingo.api.documents.Card;
import com.reactivebingo.api.documents.PlayerDocument;
import com.reactivebingo.api.documents.RoundDocument;
import com.reactivebingo.api.dtos.MailMessageDTO;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(componentModel = "spring"
        , injectionStrategy = CONSTRUCTOR
        , uses = {CardMapper.class, RoundMapper.class, PlayerMapper.class})
@DecoratedWith(MailMapperDecorator.class)
public interface MailMapper {

    @Mapping(target = "destination", source = "player.email")
    @Mapping(target = "subject", constant = "Resultado de sua Cartela no Reactive Bingo")
    @Mapping(target = "round", source = "round", qualifiedByName = "roundToResponse")
    @Mapping(target = "player", source = "player", qualifiedByName = "playerToResponse")
    @Mapping(target = "card", source = "card", qualifiedByName = "cardToDto")
    @Mapping(target = "winners", source = "round", qualifiedByName = "roundWinners")
    MailMessageDTO toDTO(final RoundDocument round, final Card card, final PlayerDocument player);

    @Mapping(target = "to", expression = "java(new String[]{mailMessageDTO.destination()})")
    @Mapping(target = "from", source = "sender")
    @Mapping(target = "subject", source = "mailMessageDTO.subject")
    @Mapping(target = "fileTypeMap", ignore = true)
    @Mapping(target = "encodeFilenames", ignore = true)
    @Mapping(target = "validateAddresses", ignore = true)
    @Mapping(target = "replyTo", ignore = true)
    @Mapping(target = "cc", ignore = true)
    @Mapping(target = "bcc", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "sentDate", ignore = true)
    @Mapping(target = "text", ignore = true)
    MimeMessageHelper toMimeMessageHelper(@MappingTarget final MimeMessageHelper helper, final MailMessageDTO mailMessageDTO,
                                          final String sender, final String body) throws MessagingException;

}
