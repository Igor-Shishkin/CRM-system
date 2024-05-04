package com.crm.system.services.utils.logUtils.facadeForLogEntry;

public enum EntryType {
    ADD_CLIENT {public String getTextForEntry(String fullName) {
        return String.format("Client %s is created", fullName);
    }},
    SENT_CLIENT_TO_BLACKLIST {public String getTextForEntry(String fullName) {
        return String.format("Client %s goes to blackList", fullName);
    }},
    RESTORE_CLIENT_FROM_BLACKLIST {public String getTextForEntry(String fullName) {
        return String.format("Client %s is restored from blackList", fullName);
    }},
    EMAIL_IS_SENT() {public String getTextForEntry(String email) {
        return String.format("Message sent. Email address: %s", email);
    }},
    SAVE_NEW_USER {public String getTextForEntry(String username) {
        return String.format("User '%s' is added", username);
    }},
    DELETE_USER {public String getTextForEntry(String username) {
        return String.format("User '%s' is deleted", username);
    }};

    public abstract String getTextForEntry(String s);

}
