package ua.edu.chdtu.deanoffice.util.comparators;

import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class StudentDegreeFullNameComparator implements Comparator<StudentDegree> {
    public int compare(StudentDegree p1, StudentDegree p2) {
        Collator ukCollator = Collator.getInstance(new Locale("uk", "UA"));
        ukCollator.setStrength(Collator.PRIMARY);
        return ukCollator.compare((p1.getStudent().getSurname() + " " + p1.getStudent().getName() + " " + p1.getStudent().getPatronimic()),
                p2.getStudent().getSurname() + " " + p2.getStudent().getName() + " " + p2.getStudent().getPatronimic()
        );
    }
}
