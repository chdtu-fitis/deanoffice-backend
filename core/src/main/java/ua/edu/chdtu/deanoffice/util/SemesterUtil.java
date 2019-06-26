package ua.edu.chdtu.deanoffice.util;

import java.time.LocalDate;

public class SemesterUtil {
    public static int getCurrentSemester(){
        LocalDate winterSessionStarts = LocalDate.of(LocalDate.now().getYear(), 12, 20);
        LocalDate winterSessionEnds = LocalDate.of(LocalDate.now().getYear(), 6, 20);
        if (LocalDate.now().isAfter(winterSessionEnds) && LocalDate.now().isBefore(winterSessionStarts))
            return  2;
        else
            return  1;
    }
}
