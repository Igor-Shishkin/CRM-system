package com.crm.system.exception;

import com.crm.system.exception.ClientAlreadyExistException;
import com.crm.system.exception.RequestOptionalIsEmpty;
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

    @ExceptionHandler({
            AuthenticationCredentialsNotFoundException.class,
            UserAlreadyExistsException.class,
            RequestOptionalIsEmpty.class,
            ClientAlreadyExistException.class})
    protected ResponseEntity<MessageResponse> handleAllExceptions(Exception exception) {
        HttpStatus status = null;
        String message = exception.getMessage();;

        if (exception instanceof AuthenticationCredentialsNotFoundException) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (exception instanceof UserAlreadyExistsException ||
                   exception instanceof ClientAlreadyExistException) {
            status = HttpStatus.CONFLICT;
        } else if (exception instanceof RequestOptionalIsEmpty){
            status = HttpStatus.NOT_FOUND;
        }

        log.error( exception.getClass() + ": " + message);
        assert status != null;
        return new ResponseEntity<>(new MessageResponse(message), status);
    }
}
