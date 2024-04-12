package com.crm.system.services.utils.logUtils;

import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.LogEntryDecorator;
import org.springframework.stereotype.Component;

@Component
public interface LogEntryFacade<T> {
    void createAndSaveMessage(T data, EntryType entryType, LogEntryDecorator... decorators);
}
