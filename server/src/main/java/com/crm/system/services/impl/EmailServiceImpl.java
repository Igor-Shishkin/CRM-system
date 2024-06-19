package com.crm.system.services.impl;

import com.crm.system.playload.request.SentEmailDTO;
import com.crm.system.services.EmailService;
import com.crm.system.services.UserService;
import com.crm.system.services.utils.logUtils.facadeForLogEntry.EntryType;
import com.crm.system.services.utils.logUtils.facadeForLogEntry.LogEntryForEmailFacade;
import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.MarkAsDoneDecorator;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final LogEntryForEmailFacade facade;
    private final UserService userService;

    public EmailServiceImpl(JavaMailSender javaMailSender,
                            LogEntryForEmailFacade facade, UserService userService) {
        this.javaMailSender = javaMailSender;
        this.facade = facade;
        this.userService = userService;
    }
    public void sentEmail(SentEmailDTO sentEmailDTO) throws UserPrincipalNotFoundException {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(sentEmailDTO.getEmail());
        message.setSubject(sentEmailDTO.getSubjectOfMail());
        message.setText(sentEmailDTO.getTextOfEmail());

        javaMailSender.send(message);

        facade.createAndSaveMessage(sentEmailDTO,
                EntryType.EMAIL_IS_SENT,
                userService.getActiveUser(),
                new MarkAsDoneDecorator());
    }
}
