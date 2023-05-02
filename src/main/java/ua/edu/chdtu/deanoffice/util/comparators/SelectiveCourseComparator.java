package ua.edu.chdtu.deanoffice.util.comparators;

import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class SelectiveCourseComparator implements Comparator<SelectiveCourse> {
    public int compare(SelectiveCourse c1, SelectiveCourse c2) {
        Collator ukCollator = Collator.getInstance(new Locale("uk", "UA"));
        ukCollator.setStrength(Collator.PRIMARY);
        return ukCollator.compare(c1.getCourse().getCourseName().getName(),
                c2.getCourse().getCourseName().getName()
        );
    }
}
