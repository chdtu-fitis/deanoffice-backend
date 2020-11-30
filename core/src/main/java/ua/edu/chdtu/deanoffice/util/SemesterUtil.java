package ua.edu.chdtu.deanoffice.util;

import java.time.LocalDate;

public class SemesterUtil {
    public static int getCurrentSemester(){
        LocalDate winterSessionStarts = LocalDate.of(LocalDate.now().getYear(), 12, 10);
        LocalDate summerSessionStarts = LocalDate.of(LocalDate.now().getYear(), 6, 10);
        if (LocalDate.now().isAfter(summerSessionStarts) && LocalDate.now().isBefore(winterSessionStarts))
            return 2;
        else
            return 1;
    }
}
