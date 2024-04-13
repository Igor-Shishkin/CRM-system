package com.crm.system.exception;

public class TextOrEmailIsEmptyException extends RuntimeException{
    public TextOrEmailIsEmptyException(String message) {
        super(message);
    }
}
