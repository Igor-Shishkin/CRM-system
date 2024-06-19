package com.crm.system.exception;

import com.crm.system.playload.response.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;
import java.nio.file.attribute.UserPrincipalNotFoundException;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            AuthenticationCredentialsNotFoundException.class,
            UserAlreadyExistsException.class,
            RequestOptionalIsEmpty.class,
            ClientAlreadyExistException.class,
            TransactionSystemException.class,
            MismanagementOfTheClientException.class,
            FileNotFoundException.class,
            UserPrincipalNotFoundException.class})
    protected ResponseEntity<MessageResponse> handleJsonExceptions(Exception exception) {
        HttpStatus status = null;
        String message = exception.getMessage();

        if (exception instanceof AuthenticationCredentialsNotFoundException) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (exception instanceof UserAlreadyExistsException ||
                exception instanceof ClientAlreadyExistException ||
                exception instanceof TransactionSystemException ||
                exception instanceof MismanagementOfTheClientException) {
            status = HttpStatus.CONFLICT;
        } else if (exception instanceof RequestOptionalIsEmpty ||
                exception instanceof FileNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (exception instanceof UserPrincipalNotFoundException) {
            status = HttpStatus.UNAUTHORIZED;
        }

        log.error(exception.getClass() + ": " + message);
        assert status != null;
        return new ResponseEntity<>(new MessageResponse(message), status);
    }




    @ExceptionHandler({
            UserDoesNotHavePhotoException.class})
    protected ResponseEntity<byte[]> handleImageExceptions(Exception exception) {
        HttpStatus status = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        if (exception instanceof UserDoesNotHavePhotoException) {
            status = HttpStatus.NOT_FOUND;
        }

        log.error(exception.getClass() + ": " + exception.getMessage());
        assert status != null;
        return new ResponseEntity<>(new byte[0], headers, status);
    }
}
