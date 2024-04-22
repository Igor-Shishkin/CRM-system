package com.crm.system.services.utils.logUtils.textFactoryLogEntry;

import com.crm.system.models.User;
import org.springframework.stereotype.Component;

@Component
public class LogEntryTextFactoryForAdministration implements LogEntryTextFactory<User>{
    @Override
    public String generateText(User user, EntryType entryType) {
        switch (entryType) {
            case SAVE_NEW_USER -> {
                return String.format("User %s is added", user.getUsername());
            }
            case DELETE_USER -> {
                return String.format("User '%s' is deleted", user.getUsername());
            }
        }
        return null;
    }
}
