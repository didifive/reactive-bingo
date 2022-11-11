package com.reactivebingo.api.dtos;

import com.reactivebingo.api.dtos.responses.PlayerResponseDTO;
import com.reactivebingo.api.dtos.responses.RoundResponseDTO;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public record MailMessageDTO(String destination,
                             String subject,
                             String template,
                             Map<String, Object> variables) {

    public static MailMessageDTOBuilder builder() {
        return new MailMessageDTOBuilder();
    }

    public static class MailMessageDTOBuilder {
        private final Map<String, Object> variables = new HashMap<>();
        private String destination;
        private String subject;

        public MailMessageDTOBuilder destination(final String destination) {
            this.destination = destination;
            return this;
        }

        public MailMessageDTOBuilder subject(final String subject) {
            this.subject = subject;
            return this;
        }

        private MailMessageDTOBuilder variables(final String key, final Object value) {
            this.variables.put(key, value);
            return this;
        }

        public MailMessageDTOBuilder player(final PlayerResponseDTO player) {
            return variables("player", player);
        }

        public MailMessageDTOBuilder card(final CardDTO card) {
            return variables("card", card);
        }

        public MailMessageDTOBuilder round(final RoundResponseDTO round) {
            return variables("round", round);
        }

        public MailMessageDTOBuilder winners(final Long winners) {
            return variables("winners", winners);
        }

        public MailMessageDTO build() {
            return new MailMessageDTO(destination, subject, "mail/roundResult", variables);
        }

    }

}
