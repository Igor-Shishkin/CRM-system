package com.crm.system.exception;

public class NameOrEmailIsEmptyException extends RuntimeException{
    public NameOrEmailIsEmptyException(String message) {
        super(message);
    }
}
