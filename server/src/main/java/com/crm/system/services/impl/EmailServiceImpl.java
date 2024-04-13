package com.crm.system.services.impl;

import com.crm.system.playload.request.SentEmailDTO;
import com.crm.system.services.EmailService;
import com.crm.system.services.utils.logUtils.textFactoryLogEntry.EntryType;
import com.crm.system.services.utils.logUtils.facadeForLogEntry.LogEntryForEmailFacade;
import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.MarkAsDoneDecorator;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final LogEntryForEmailFacade facade;

    public EmailServiceImpl(JavaMailSender javaMailSender,
                            LogEntryForEmailFacade facade) {
        this.javaMailSender = javaMailSender;
        this.facade = facade;
    }
    public void sentEmail(SentEmailDTO sentEmailDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(sentEmailDTO.getEmail());
        message.setSubject(sentEmailDTO.getSubjectOfMail());
        message.setText(sentEmailDTO.getTextOfEmail());

        javaMailSender.send(message);

        facade.createAndSaveMessage(sentEmailDTO,
                EntryType.EMAIL_IS_SENT,
                new MarkAsDoneDecorator());
    }
}
