package com.crm.system.exception;

public class UserDoesNotHavePhotoException extends RuntimeException {
    public UserDoesNotHavePhotoException(String message) {
        super(message);
    }
}
