package com.crm.system.services.utils.logUtils.facadeForLogEntry;

import com.crm.system.models.User;
import com.crm.system.models.logForUser.LogEntry;
import com.crm.system.models.logForUser.TagName;
import com.crm.system.playload.request.SentEmailDTO;
import com.crm.system.services.LogEntryService;
import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.LogEntryDecorator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Slf4j
@Component
public class LogEntryForEmailFacade implements LogEntryFacade<SentEmailDTO> {
    private final LogEntryService logEntryService;

    public LogEntryForEmailFacade(LogEntryService logEntryService) {
        this.logEntryService = logEntryService;
    }
    @Override
    public void createAndSaveMessage(SentEmailDTO emailDTO,
                                     EntryType entryType,
                                     LogEntryDecorator... decorators) throws UserPrincipalNotFoundException {

        LogEntry logEntry = new LogEntry.Builder()
                .withText(entryType.getTextForEntry(emailDTO.getEmail()))
                .withTagName(TagName.EMAIL)
                .withAdditionalInformation(emailDTO.getTextOfEmail())
                .build();

        for (LogEntryDecorator decorator : decorators) {
            logEntry = decorator.decorate(logEntry);
        }

        logEntryService.automaticallyCreateMessage(logEntry);
    }
}
