package ua.edu.chdtu.deanoffice.exception;

public class UnauthorizedFacultyDataException extends Exception {
    public UnauthorizedFacultyDataException() {
        super("Даному факультету заборонений доступ до цих даних");
    }

    public UnauthorizedFacultyDataException(String message) {
        super(message);
    }
}
