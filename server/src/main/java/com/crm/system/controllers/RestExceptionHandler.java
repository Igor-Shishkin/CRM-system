package com.crm.system.controllers;

import com.crm.system.exception.UserAlreadyExistsException;
import com.crm.system.playload.response.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<MessageResponse> handleAllExceptions(Exception exception) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message;

        if (exception instanceof AuthenticationCredentialsNotFoundException) {
            status = HttpStatus.NOT_ACCEPTABLE;
            message = "Authorisation Error: " + exception.getMessage();
        } else if (exception instanceof UserAlreadyExistsException) {
            status = HttpStatus.FORBIDDEN;
            message = "Registration error: " + exception.getMessage();
        } else  {
            message = exception.getMessage();
        }

        log.error(message);
        return new ResponseEntity<>(new MessageResponse(message), status);
    }
}
