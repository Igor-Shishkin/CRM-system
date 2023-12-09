package com.crm.system.exception;

public class SubjectNotBelongToActiveUser extends RuntimeException {

    public SubjectNotBelongToActiveUser(String message) {
        super(message);
    }
}
