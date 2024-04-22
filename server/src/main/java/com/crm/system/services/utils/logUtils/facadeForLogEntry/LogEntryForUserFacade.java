package com.crm.system.services.utils.logUtils.facadeForLogEntry;

import com.crm.system.models.User;
import com.crm.system.models.logForUser.LogEntry;
import com.crm.system.models.logForUser.TagName;
import com.crm.system.services.LogEntryService;
import com.crm.system.services.UserService;
import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.LogEntryDecorator;
import com.crm.system.services.utils.logUtils.textFactoryLogEntry.EntryType;
import com.crm.system.services.utils.logUtils.textFactoryLogEntry.LogEntryTextFactory;
import com.crm.system.services.utils.logUtils.textFactoryLogEntry.LogEntryTextFactoryForAdministration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Slf4j
@Component
public class LogEntryForUserFacade implements LogEntryFacade<User> {

    private final LogEntryService logEntryService;
    private final LogEntryTextFactory<User> logEntryTextFactory;
    private final UserService userService;

    public LogEntryForUserFacade(LogEntryService logEntryService, LogEntryTextFactoryForAdministration logEntryTextFactory, UserService userService) {
        this.logEntryService = logEntryService;
        this.logEntryTextFactory = logEntryTextFactory;
        this.userService = userService;
    }

    @Override
    public void createAndSaveMessage(User user, EntryType entryType, LogEntryDecorator... decorators) {
        assert (user != null);
        String text = logEntryTextFactory.generateText(user, entryType);

        LogEntry logEntry = new LogEntry.Builder()
                .withText(text)
                .withTagId(user.getUserId())
                .withTagName(TagName.ADMINISTRATION)
                .withUser(getActiveUser())
                .build();

        for (LogEntryDecorator decorator : decorators) {
            logEntry = decorator.decorate(logEntry);
        }

        logEntryService.automaticallyCreateMessage(logEntry);
    }
    private User getActiveUser() {
        User user = null;
        try {
            user = userService.getActiveUser();
        } catch (UserPrincipalNotFoundException e){
            log.error(this.getClass() + ". Exception: " + e);
        }
        return user;
    }
}
