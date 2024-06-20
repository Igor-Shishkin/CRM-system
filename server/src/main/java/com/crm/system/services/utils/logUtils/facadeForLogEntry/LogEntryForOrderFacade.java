package com.crm.system.services.utils.logUtils.facadeForLogEntry;

import com.crm.system.models.User;
import com.crm.system.models.logForUser.LogEntry;
import com.crm.system.models.logForUser.TagName;
import com.crm.system.models.order.Order;
import com.crm.system.services.LogEntryService;
import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.LogEntryDecorator;

import java.nio.file.attribute.UserPrincipalNotFoundException;

public class LogEntryForOrderFacade implements LogEntryFacade<Order>{
    private final LogEntryService logEntryService;

    public LogEntryForOrderFacade(LogEntryService logEntryService) {
        this.logEntryService = logEntryService;
    }


    @Override
    public void createAndSaveMessage(Order order,
                                     EntryType entryType,
                                     LogEntryDecorator... decorators) throws UserPrincipalNotFoundException {

        LogEntry logEntry = new LogEntry.Builder()
                .withText(entryType.getTextForEntry(order.getClient().getFullName()))
                .withTagId(order.getClient().getClientId())
                .withTagName(TagName.CLIENT)
                .build();

        for (LogEntryDecorator decorator : decorators) {
            logEntry = decorator.decorate(logEntry);
        }

        logEntryService.automaticallyCreateMessage(logEntry);
    }
}
