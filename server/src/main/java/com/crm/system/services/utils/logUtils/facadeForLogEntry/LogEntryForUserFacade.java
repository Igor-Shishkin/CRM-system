package com.crm.system.services.utils.logUtils.facadeForLogEntry;

import com.crm.system.models.User;
import com.crm.system.models.logForUser.LogEntry;
import com.crm.system.models.logForUser.TagName;
import com.crm.system.services.LogEntryService;
import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.LogEntryDecorator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class LogEntryForUserFacade implements LogEntryFacade<User> {

    private final LogEntryService logEntryService;
    public LogEntryForUserFacade(LogEntryService logEntryService) {
        this.logEntryService = logEntryService;
    }

    @Override
    public void createAndSaveMessage(User user,
                                     EntryType entryType,
                                     User activeUser,
                                     LogEntryDecorator... decorators) {
        LogEntry logEntry = new LogEntry.Builder()
                .withText(entryType.getTextForEntry(user.getUsername()))
                .withTagId(user.getUserId())
                .withTagName(TagName.ADMINISTRATION)
                .withUser(activeUser)
                .build();

        for (LogEntryDecorator decorator : decorators) {
            logEntry = decorator.decorate(logEntry);
        }

        logEntryService.automaticallyCreateMessage(logEntry);
    }
}
