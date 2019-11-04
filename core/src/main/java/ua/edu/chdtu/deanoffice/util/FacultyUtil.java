package ua.edu.chdtu.deanoffice.util;

import org.springframework.security.core.context.SecurityContextHolder;
import ua.edu.chdtu.deanoffice.entity.Faculty;

public class FacultyUtil {
    public static String getUserFacultyId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
    }

    public static int getUserFacultyIdInt() {
        return Integer.parseInt((String) SecurityContextHolder.getContext().getAuthentication().getDetails());
    }

    public static String getRefinedFacultyName(Faculty faculty) {
        String defaultFacultyName = faculty.getName();
        String fixedFacultyName = defaultFacultyName.replace("факультет","").trim();

        return fixedFacultyName;
    }
}
