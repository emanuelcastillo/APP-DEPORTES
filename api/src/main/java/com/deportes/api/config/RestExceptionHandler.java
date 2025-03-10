package com.deportes.api.config;

import com.deportes.api.exception.CustomValidationException;
import com.deportes.api.records.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ResponseBody<?>> handleException(Exception e) {
        if (e.getCause() instanceof CustomValidationException c) {
            log.error("Exception: {}", c.getMessage());
            return new ResponseEntity<>(
                    new ResponseBody<>(e.getMessage() == null ? "UNKNOWN ERROR" : e.getMessage(), null),
                    HttpStatus.NOT_FOUND);
        }
        log.error("Exception: ", e);
        return new ResponseEntity<>(
                new ResponseBody<>(e.getMessage() == null ? "UNKNOWN ERROR" : e.getMessage(), null),
                HttpStatus.NOT_FOUND);
    }

}