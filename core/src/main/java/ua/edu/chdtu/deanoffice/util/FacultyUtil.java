package ua.edu.chdtu.deanoffice.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class FacultyUtil {
    public static String getUserFacultyId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
    }

    public static int getUserFacultyIdInt() {
        return Integer.parseInt((String) SecurityContextHolder.getContext().getAuthentication().getDetails());
    }
}
