package com.crm.system.services.utils.logUtils.decoratorsForLogEntry;

import com.crm.system.models.logForUser.LogEntry;
import org.springframework.stereotype.Component;

@Component
public interface LogEntryDecorator {
    LogEntry decorate(LogEntry logEntry);
}
