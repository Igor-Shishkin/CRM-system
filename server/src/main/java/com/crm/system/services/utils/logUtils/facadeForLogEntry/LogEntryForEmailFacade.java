package com.crm.system.services.utils.logUtils.facadeForLogEntry;

import com.crm.system.models.User;
import com.crm.system.models.logForUser.LogEntry;
import com.crm.system.models.logForUser.TagName;
import com.crm.system.playload.request.SentEmailDTO;
import com.crm.system.services.LogEntryService;
import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.LogEntryDecorator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
                                     User user,
                                     LogEntryDecorator... decorators){

        LogEntry logEntry = new LogEntry.Builder()
                .withText(entryType.getTextForEntry(emailDTO.getEmail()))
                .withTagName(TagName.EMAIL)
                .withAdditionalInformation(emailDTO.getTextOfEmail())
                .withUser(user)
                .build();

        for (LogEntryDecorator decorator : decorators) {
            logEntry = decorator.decorate(logEntry);
        }

        logEntryService.automaticallyCreateMessage(logEntry);
    }
}
