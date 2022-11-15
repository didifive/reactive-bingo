package com.reactivebingo.api.services;

import com.github.javafaker.Faker;
import com.icegreen.greenmail.util.GreenMail;
import com.reactivebingo.api.configs.RetryConfig;
import com.reactivebingo.api.dtos.mappers.*;
import com.reactivebingo.api.utils.RetryHelper;
import com.reactivebingo.api.utils.TemplateMailConfigStub;
import com.reactivebingo.api.utils.extension.mail.MailSender;
import com.reactivebingo.api.utils.extension.mail.MailServer;
import com.reactivebingo.api.utils.extension.mail.MailServerExtension;
import com.reactivebingo.api.utils.extension.mail.SMTPPort;
import com.reactivebingo.api.utils.factorybot.dtos.CardDTOFactoryBot;
import com.reactivebingo.api.utils.factorybot.dtos.MailMessageDTOFactoryBot;
import com.reactivebingo.api.utils.factorybot.dtos.responses.PlayerResponseDTOFactoryBot;
import com.reactivebingo.api.utils.factorybot.dtos.responses.RoundResponseDTOFactoryBot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.util.Arrays;

import static com.reactivebingo.api.utils.factorybot.RandomData.getFaker;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {MailMapperImpl.class
        , MailMapperImpl_.class
        , CardMapperImpl.class
        , RoundMapperImpl.class
        , PlayerMapperImpl.class
        , DrawnNumberMapperImpl.class})
@ExtendWith({SpringExtension.class, MailServerExtension.class})
class MailServiceTest {

    @SMTPPort
    private final int port = 1234;
    private final RetryHelper retryHelper = new RetryHelper(new RetryConfig(1L, 1L));
    private final Faker faker = getFaker();
    private final String sender = faker.internet().emailAddress();
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MailMapper mailMapper;
    private MailService mailService;
    private GreenMail smtpServer;

    @BeforeEach
    void setup(@MailServer final GreenMail smtpServer, @MailSender final JavaMailSender mailSender) {
        this.smtpServer = smtpServer;
        var templateEngine = TemplateMailConfigStub.templateEngine(applicationContext);
        mailService = new MailService(retryHelper, mailSender, templateEngine, mailMapper, sender);
    }


    @Test
    void sendTest() throws MessagingException {
        var playerResponseDTO = PlayerResponseDTOFactoryBot.builder().build();
        var cardDTO = CardDTOFactoryBot.builder().withPlayerId(playerResponseDTO.id()).build();
        var roundResponseDTO = RoundResponseDTOFactoryBot.builder()
                .withDrawnNumbers(90)
                .withCard(cardDTO)
                .build();
        var mailMessage = MailMessageDTOFactoryBot.builder(roundResponseDTO, cardDTO, playerResponseDTO).build();
        StepVerifier.create(mailService.send(mailMessage)).verifyComplete();
        assertThat(smtpServer.getReceivedMessages().length).isOne();
        var message = Arrays.stream(smtpServer.getReceivedMessages()).findFirst().orElseThrow();
        assertThat(message.getSubject()).isEqualTo(mailMessage.subject());
        assertThat(message.getRecipients(Message.RecipientType.TO)).contains(new InternetAddress(mailMessage.destination()));
        assertThat(message.getHeader("FROM")).contains(sender);
    }

}
