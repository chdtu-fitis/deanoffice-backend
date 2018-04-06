package ua.edu.chdtu.deanoffice.api.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    private static final HttpStatus DEFAULT_RESPONSE_STATUS = HttpStatus.UNPROCESSABLE_ENTITY;
    private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(Exception.class)
    public static ResponseEntity handleException(String message, HttpStatus responseStatus) {
        logger.error(message);
        return ResponseEntity.status(responseStatus).body(message);
    }

    public static ResponseEntity handleException(Exception exception) {
        return handleException(exception.getMessage(), DEFAULT_RESPONSE_STATUS);
    }

    public static ResponseEntity handleException(String message) {
        return handleException(message, DEFAULT_RESPONSE_STATUS);
    }
}
