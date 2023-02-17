package com.tgshelterbot.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Slf4j
public class ExceptionHandler {
    /**
     * Обработка NOT_FOUND исключений и проброс текста
     * ошибки в контроллер
     *
     * @param e Exception
     * @return ResponseEntity
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException e) {
        log.error("Not found exception occurred, message: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
    }

    /**
     * Обработка FORBIDDEN исключений и проброс текста
     * ошибки в контроллер
     *
     * @param e Exception
     * @return ResponseEntity
     */
    @ResponseStatus(FORBIDDEN)
    @org.springframework.web.bind.annotation.ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<String> handleEntityExist(EntityExistsException e) {
        log.error("EntityExistsException occurred, message: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), FORBIDDEN);
    }

    /**
     * Обработка NOT_ACCEPTABLE исключений и проброс текста
     * ошибки в контроллер
     *
     * @param e Exception
     * @return ResponseEntity
     */
    @ResponseStatus(NOT_ACCEPTABLE)
    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArg(IllegalArgumentException e) {
        log.error("IllegalArgumentException occurred, message: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), NOT_ACCEPTABLE);
    }
}
