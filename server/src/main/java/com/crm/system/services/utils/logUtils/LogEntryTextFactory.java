package com.crm.system.services.utils.logUtils;

import com.crm.system.models.Client;
import org.springframework.stereotype.Component;

@Component
public interface LogEntryTextFactory {
    String generateText(Client client, EntryType entryType);
}
