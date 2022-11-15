package com.reactivebingo.api.utils.factorybot.dtos;

import com.reactivebingo.api.dtos.CardDTO;
import com.reactivebingo.api.dtos.MailMessageDTO;
import com.reactivebingo.api.dtos.responses.PlayerResponseDTO;
import com.reactivebingo.api.dtos.responses.RoundResponseDTO;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MailMessageDTOFactoryBot {

    public static MailMessageDTOFactoryBotBuilder builder(final RoundResponseDTO round, final CardDTO card, final PlayerResponseDTO player) {
        return new MailMessageDTOFactoryBotBuilder(round, card, player);
    }

    public static class MailMessageDTOFactoryBotBuilder {

        private final RoundResponseDTO round;
        private final CardDTO card;
        private final PlayerResponseDTO player;
        private final String destination;
        private final String subject;
        private final Map<String, Object> variables = new HashMap<>();

        public MailMessageDTOFactoryBotBuilder(final RoundResponseDTO round, final CardDTO card, final PlayerResponseDTO player) {
            var faker = getFaker();
            this.destination = faker.internet().emailAddress();
            this.subject = faker.chuckNorris().fact();
            this.round = round;
            this.card = card;
            this.player = player;
        }

        public MailMessageDTO build() {
            return MailMessageDTO.builder()
                    .destination(destination)
                    .subject(subject)
                    .round(round)
                    .card(card)
                    .player(player)
                    .build();
        }

    }

}
