package ua.edu.chdtu.deanoffice.webstarter;

import java.io.Serializable;

public class ErrorMessageResponse {
    private final String message;

    public ErrorMessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}