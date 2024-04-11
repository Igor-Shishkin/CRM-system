package com.crm.system.services.utils.logUtils.decoratorsForLogEntry;

import com.crm.system.models.logForUser.LogEntry;

public class MarkAsImportantDecorator implements LogEntryDecorator{
    @Override
    public LogEntry decorate(LogEntry logEntry) {

        return new LogEntry.Builder(logEntry)
                .withIsImportant(true)
                .build();
    }
}
