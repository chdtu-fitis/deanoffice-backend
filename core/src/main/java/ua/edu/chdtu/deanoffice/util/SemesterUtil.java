package ua.edu.chdtu.deanoffice.util;

import java.time.LocalDate;

public class SemesterUtil {
    public static int getCurrentSemester(){
        LocalDate winterSessionStarts = LocalDate.of(LocalDate.now().getYear(), 12, 10);
        LocalDate winterSessionEnds = LocalDate.of(LocalDate.now().getYear(), 6, 10);
        if (LocalDate.now().isAfter(winterSessionEnds) && LocalDate.now().isBefore(winterSessionStarts))
            return  0;
        else
            return  1;
    }
}
