package com.crm.system.services.utils.logUtils.facadeForLogEntry;

import com.crm.system.services.utils.logUtils.decoratorsForLogEntry.LogEntryDecorator;
import com.crm.system.services.utils.logUtils.textFactoryLogEntry.EntryType;
import org.springframework.stereotype.Component;

@Component
public interface LogEntryFacade<T> {
    void createAndSaveMessage(T data, EntryType entryType, LogEntryDecorator... decorators);
}
