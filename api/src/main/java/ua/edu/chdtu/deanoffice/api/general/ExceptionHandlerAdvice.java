package ua.edu.chdtu.deanoffice.api.general;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.NotFoundException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * exceptionLocation -- клас, в якому була виловлена помилка (найчастіше контроллер).
 * Приклад:
 * try {
 *      ...
 *  } catch(Exception exception) {
 *     return handleException(exception, StudentController.class)
 * }
 */

@ControllerAdvice
public class ExceptionHandlerAdvice {

    private static final HttpStatus DEFAULT_RESPONSE_STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

    @ExceptionHandler(Exception.class)
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
