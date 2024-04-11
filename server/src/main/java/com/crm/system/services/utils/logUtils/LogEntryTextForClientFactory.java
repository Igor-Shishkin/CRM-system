package com.crm.system.services.utils.logUtils;

import com.crm.system.models.Client;
import org.springframework.stereotype.Component;

@Component
public class LogEntryTextForClientFactory implements LogEntryTextFactory{
    @Override
    public String generateText(Client client, EntryType entryType) {
        switch (entryType) {
            case ADD_CLIENT -> {
                return String.format("Client %s is created", client.getFullName());
            }
            case RESTORE_CLIENT_FROM_BLACKLIST -> {
                return String.format("Client %s is restored from blackList", client.getFullName());
            }
            case SENT_CLIENT_TO_BLACKLIST -> {
                return String.format("Client %s goes to blackList", client.getFullName());
            }
            default -> {
                return null;
            }
        }
    }
}
