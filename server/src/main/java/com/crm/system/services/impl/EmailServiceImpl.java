package com.crm.system.services.impl;

import com.crm.system.models.history.LogEntry;
import com.crm.system.playload.request.SentEmailDTO;
import com.crm.system.services.EmailService;
import com.crm.system.services.LogEntryService;
import com.crm.system.services.UserService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Service
public class EmailServiceImpl implements EmailService {
    private final LogEntryService logEntryService;
    private final JavaMailSender javaMailSender;
    private final UserService userService;

    public EmailServiceImpl(UserService userService, LogEntryService logEntryService,
                        JavaMailSender javaMailSender) {
        this.logEntryService = logEntryService;
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

        logEntryService.automaticallyCreateMessage(new LogEntry.Builder()
                .withMessageText(messageText)
                .withIsDone(true)
                .withIsImportant(false)
                .withTagName(sentEmailDTO.getTagName())
                .withTagId(sentEmailDTO.getTagId())
                .withUser(userService.getActiveUser())
                .build());
    }
}
