package ua.edu.chdtu.deanoffice.api.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.NotFoundException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import javax.validation.ConstraintViolationException;
import static org.slf4j.LoggerFactory.getLogger;

@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);
    private static final HttpStatus DEFAULT_RESPONSE_STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

    @ExceptionHandler(value = {ConstraintViolationException.class})
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException e, WebRequest request) {
        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {UnauthorizedFacultyDataException.class})
    protected ResponseEntity<Object> handleUnauthorizedFacultyData(UnauthorizedFacultyDataException e, WebRequest request) {
        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = {OperationCannotBePerformedException.class})
    protected ResponseEntity<Object> handleOperationCannotBePerformed(OperationCannotBePerformedException e, WebRequest request) {
        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(value = {NotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(NotFoundException e, WebRequest request) {
        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleGeneralException(Exception e, WebRequest request) {
        logger.error("ERROR", e);
        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    public static ResponseEntity handleException(String message, Class exceptionLocation, HttpStatus responseStatus) {
        getLogger(exceptionLocation).error(message);
        return ResponseEntity.status(responseStatus).body(message);
    }

    public static ResponseEntity handleException(Exception exception, Class exceptionLocation) {
        return handleException(exception.getMessage(), exceptionLocation, DEFAULT_RESPONSE_STATUS);
    }

    public static ResponseEntity handleException(Exception exception, Class exceptionLocation, HttpStatus responseStatus) {
        if (isStackTraceReported(exception))
            getLogger(exceptionLocation).error("ERROR", exception);
        return ResponseEntity.status(responseStatus).body(exception.getMessage());
    }

    private static boolean isStackTraceReported(Exception e) {
        if (e instanceof NotFoundException
                || e instanceof UnauthorizedFacultyDataException
                || e instanceof OperationCannotBePerformedException)
            return false;
        else
            return true;
    }
}
