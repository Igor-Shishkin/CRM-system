package com.crm.system.services;

import com.crm.system.models.history.HistoryMessage;
import com.crm.system.playload.request.SentEmailDTO;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Service
public class EmailService {

    private final HistoryMessageService historyMessageService;
    private final JavaMailSender javaMailSender;
    private final UserService userService;

    public EmailService(UserService userService, HistoryMessageService historyMessageService,
                        JavaMailSender javaMailSender) {
        this.historyMessageService = historyMessageService;
        this.javaMailSender = javaMailSender;
        this.userService = userService;
    }


    public void sentEmail(SentEmailDTO sentEmailDTO) throws UserPrincipalNotFoundException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(sentEmailDTO.getEmail());
        message.setSubject(sentEmailDTO.getSubjectOfMail());
        message.setText(sentEmailDTO.getTextOfEmail());

        javaMailSender.send(message);

        createHistoryMessage(sentEmailDTO);
    }

    private void createHistoryMessage(SentEmailDTO sentEmailDTO) throws UserPrincipalNotFoundException {
        String messageText = "Send email: " + sentEmailDTO.getSubjectOfMail() + "'";

        historyMessageService.automaticallyCreateMessage(new HistoryMessage.Builder()
                .withMessageText(messageText)
                .withIsDone(true)
                .withIsImportant(false)
                .withTagName(sentEmailDTO.getTagName())
                .withTagId(sentEmailDTO.getTagId())
                .withUser(userService.getActiveUser())
                .build());
    }
}
