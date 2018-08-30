package ua.edu.chdtu.deanoffice.api.general;

import org.springframework.http.HttpStatus;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.PageNotFoundException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;

public class ExceptionToHttpCodeMapUtil {
    public static HttpStatus map(Exception e) {
        if (e instanceof PageNotFoundException)
            return HttpStatus.NOT_FOUND;
        if (e instanceof UnauthorizedFacultyDataException)
            return HttpStatus.UNAUTHORIZED;
        if (e instanceof OperationCannotBePerformedException)
            return HttpStatus.SERVICE_UNAVAILABLE;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
