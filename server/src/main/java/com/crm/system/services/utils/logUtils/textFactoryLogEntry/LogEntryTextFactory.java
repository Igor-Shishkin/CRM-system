package com.crm.system.services.utils.logUtils.textFactoryLogEntry;

import org.springframework.stereotype.Component;

@Component
public interface LogEntryTextFactory <T> {
    String generateText(T client, EntryType entryType);
}
