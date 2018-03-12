package ua.edu.chdtu.deanoffice.api.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    private static final HttpStatus defaultResponseStatus = HttpStatus.UNPROCESSABLE_ENTITY;
    private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(Exception.class)
    public static ResponseEntity handleException(Exception exception, HttpStatus responseStatus) {
        logger.error(exception.getMessage());
        return ResponseEntity
                .status(responseStatus)
                .body(exception.getMessage());
    }

    public static ResponseEntity handleException(Exception exception) {
        return handleException(exception, defaultResponseStatus);
    }
}
