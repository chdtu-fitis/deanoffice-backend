package ua.edu.chdtu.deanoffice.util;

import java.time.LocalDate;

public class SemesterUtil {
    public static int getCurrentSemester(){
        LocalDate winterSessionStarts = LocalDate.of(LocalDate.now().getYear(), 12, 1);
        LocalDate summerSessionStarts = LocalDate.of(LocalDate.now().getYear(), 5, 25);
        if (LocalDate.now().isAfter(summerSessionStarts) && LocalDate.now().isBefore(winterSessionStarts))
            return 2;
        else
            return 1;
    }
}
