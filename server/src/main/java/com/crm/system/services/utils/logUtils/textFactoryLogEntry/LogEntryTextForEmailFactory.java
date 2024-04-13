package com.crm.system.services.utils.logUtils.textFactoryLogEntry;

import com.crm.system.playload.request.SentEmailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogEntryTextForEmailFactory implements LogEntryTextFactory<SentEmailDTO> {

    @Override
    public String generateText(SentEmailDTO emailDTO, EntryType entryType) {
        switch (entryType) {
            case EMAIL_IS_SENT -> {
                return String.format("Message sent. Subject: %s. Email: %s",
                        emailDTO.getSubjectOfMail(),
                        emailDTO.getEmail());
            }
            default -> {
                return "";
            }
        }
    }
}
