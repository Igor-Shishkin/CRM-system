package com.crm.system.services.utils.logUtils.facadeForLogEntry;

public enum EntryType {

    ADD_CLIENT (s -> "Client '"+ s +"' is created"),

    SENT_CLIENT_TO_BLACKLIST (s -> "Client '" + s + "' goes to blackList"),

    RESTORE_CLIENT_FROM_BLACKLIST (s -> "Client '"+ s + "' is restored from blackList"),

    EMAIL_IS_SENT(s -> "Message sent. Email address: " + s),

    SAVE_NEW_USER (s -> "User '" + s + "' is added"),

    DELETE_USER (s -> "User '"+ s + "' is deleted"),

    SIGN_AGREEMENT_FOR_ORDER(s -> "Signed an agreement with " + s),

    CONFIRM_AGREEMENT (s -> "You have confirmed payment by" + s),

    CANCEL_PAYMENT (s -> "You have canceled payment for " + s),

    CANCEL_AGREEMENT (s -> "The contract with '" + s + "' clint was canceled"),

    CREATE_NEW_ORDER (s -> "You have a new order from '" + s + "' client. Good job.");


    private final EntryTextProvider textProvider;

    EntryType(EntryTextProvider textProvider) {
        this.textProvider = textProvider;
    }

    public String getTextForEntry(String s) {
        return textProvider.getText(s);
    }

    @FunctionalInterface
    private interface EntryTextProvider {
        String getText(String s);
    }

}
