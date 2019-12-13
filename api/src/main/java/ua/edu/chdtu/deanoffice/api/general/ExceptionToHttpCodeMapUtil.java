package ua.edu.chdtu.deanoffice.api.general;

import org.springframework.http.HttpStatus;
import ua.edu.chdtu.deanoffice.exception.NotFoundException;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;

public class ExceptionToHttpCodeMapUtil {
    public static HttpStatus map(Exception e) {
        if (e instanceof NotFoundException)
            return HttpStatus.NOT_FOUND;
        if (e instanceof UnauthorizedFacultyDataException)
            return HttpStatus.FORBIDDEN;
        if (e instanceof OperationCannotBePerformedException)
            return HttpStatus.UNPROCESSABLE_ENTITY;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
