package ua.edu.chdtu.deanoffice.service.document.report.groupgrade;

import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.StudentSummary;

import java.util.List;

public class StudentSummaryForGroup extends StudentSummary{


    public StudentSummaryForGroup(StudentDegree studentDegree, List<List<Grade>> grades) {
        super(studentDegree, grades);
    }


}
