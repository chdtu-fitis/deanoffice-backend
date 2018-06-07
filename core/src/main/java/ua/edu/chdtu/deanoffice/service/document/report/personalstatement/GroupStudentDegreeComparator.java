package ua.edu.chdtu.deanoffice.service.document.report.personalstatement;

import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.util.comparators.StudentDegreeFullNameComparator;

import java.util.Comparator;

public class GroupStudentDegreeComparator implements Comparator<StudentDegree> {
    public int compare(StudentDegree p1, StudentDegree p2) {
        int result = p1.getStudentGroup().getName().compareTo(p2.getStudentGroup().getName());
        if (result == 0) {
            StudentDegreeFullNameComparator studentDegreeFullNameComparator = new StudentDegreeFullNameComparator();
            return studentDegreeFullNameComparator.compare(p1, p2);
        } else {
            return result;
        }
    }
}
