package com.tgshelterbot.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@ControllerAdvice
@Slf4j
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleEntityNotFound(EntityNotFoundException e) {
        log.error("Not found exception occurred cause: {}, message: {}", e.getCause(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @org.springframework.web.bind.annotation.ExceptionHandler(EntityExistsException.class)
    public void handleEntityExistAndIllegalArg(EntityExistsException e) {
        log.error("EntityExistsException occurred cause: {}, message: {}", e.getCause(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArg(IllegalArgumentException e) {
        log.error("IllegalArgumentException occurred cause: {}, message: {}", e.getCause(), e.getMessage());
    }
}
