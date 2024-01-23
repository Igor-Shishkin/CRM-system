package com.crm.system.services;

import com.crm.system.playload.request.SentEmailDTO;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Service
public class EmailService {

    private final HistoryMessageService historyMessageService;
    private final JavaMailSender javaMailSender;

    public EmailService(UserService userService, HistoryMessageService historyMessageService, JavaMailSender javaMailSender) {
        this.historyMessageService = historyMessageService;
        this.javaMailSender = javaMailSender;
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
        String messageText = "Send email '" + sentEmailDTO.getSubjectOfMail() + "'";
        String messageNote = sentEmailDTO.getTextOfEmail();

        historyMessageService.createHistoryMessageWithTagInfo(messageText, messageNote, sentEmailDTO.getTagName(),
                sentEmailDTO.getTagId(), false, true);
    }
}
