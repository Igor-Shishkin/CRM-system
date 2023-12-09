package com.crm.system.exception;

public class RequestOptionalIsEmpty extends RuntimeException{

    public RequestOptionalIsEmpty(String message) {
        super(message);
    }
}
