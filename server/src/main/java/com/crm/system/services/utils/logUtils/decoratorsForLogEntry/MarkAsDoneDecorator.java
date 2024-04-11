package com.crm.system.services.utils.logUtils.decoratorsForLogEntry;

import com.crm.system.models.logForUser.LogEntry;

public class MarkAsDoneDecorator implements LogEntryDecorator{
    @Override
    public LogEntry decorate(LogEntry logEntry) {

        return new LogEntry.Builder(logEntry)
                .withIsDone(true)
                .build();
    }
}
