package ua.edu.chdtu.deanoffice.service.document.report.academic.reference;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import java.util.*;

@Getter
@Setter
public class StudentSummaryForAcademicReference {

    Map<Integer, SemesterDetails> semesters;
    private StudentDegree studentDegree;

    public StudentSummaryForAcademicReference(StudentDegree studentDegree, List<List<Grade>> allGrades) {
        this.studentDegree = studentDegree;
        semesters = new TreeMap<>();
        for (List<Grade> gradeList : allGrades) {
            for (Grade grade : gradeList) {
                if (isGradeValid(grade)) {
                    Integer currentSemester = grade.getCourse().getSemester();
                    int knowledgeControlId = grade.getCourse().getKnowledgeControl().getId();
                    if (!semesters.containsKey(currentSemester)) {
                        semesters.put(currentSemester, new SemesterDetails());
                    }
                    SemesterDetails semesterDetails = semesters.get(currentSemester);
                    if (knowledgeControlId == Constants.EXAM ||
                            knowledgeControlId == Constants.CREDIT || knowledgeControlId == Constants.DIFFERENTIATED_CREDIT) {
                        semesterDetails.getGrades().get(0).add(grade);
                    }
                    if (knowledgeControlId == Constants.COURSEWORK || knowledgeControlId == Constants.COURSE_PROJECT) {
                        semesterDetails.getGrades().get(1).add(grade);
                    }
                    if (knowledgeControlId == Constants.INTERNSHIP || knowledgeControlId == Constants.NON_GRADED_INTERNSHIP) {
                        semesterDetails.getGrades().get(2).add(grade);
                    }
                    if (knowledgeControlId == Constants.ATTESTATION || knowledgeControlId == Constants.STATE_EXAM) {
                        semesterDetails.getGrades().get(3).add(grade);
                    }
                    semesters.replace(currentSemester, semesterDetails);
                }
            }
        }
    }

    private boolean isGradeValid(Grade grade) {
        return grade != null && grade.getGrade() != null && grade.getPoints() != null && grade.getGrade() != 0;
    }
}
