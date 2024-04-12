package com.crm.system.services.utils.logUtils;

import org.springframework.stereotype.Component;

@Component
public interface LogEntryTextFactory <T> {
    String generateText(T client, EntryType entryType);
}
