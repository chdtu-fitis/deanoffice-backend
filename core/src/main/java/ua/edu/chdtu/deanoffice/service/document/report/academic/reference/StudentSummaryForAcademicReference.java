package ua.edu.chdtu.deanoffice.service.document.report.academic.reference;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
public class StudentSummaryForAcademicReference {

    List<SemesterDetails> semesters;
    private StudentDegree studentDegree;

    public StudentSummaryForAcademicReference(StudentDegree studentDegree, List<List<Grade>> allGrades) {
        this.studentDegree = studentDegree;
        semesters = new ArrayList<>();
        int gradesIndex = 0;
        for (List<Grade> grades : allGrades) {
            fillSemesterWithSpecificGrades(grades, gradesIndex);
            gradesIndex++;
        }
    }

    private void fillSemesterWithSpecificGrades(List<Grade> grades, int gradesIndex) {
        Map<Integer, List<Grade>> utilMap = new TreeMap<>();
        for (Grade grade : grades) {
            int semester = grade.getCourse().getSemester();
            if (checkGradeValidity(grade)) {
                utilMap.computeIfAbsent(semester, k -> new ArrayList<>());
                utilMap.get(semester).add(grade);
            }
        }

        for (Integer semester : utilMap.keySet()) {
            if (semester - 1 >= semesters.size()) {
                semesters.add(new SemesterDetails());
            }
            SemesterDetails semesterDetails = semesters.get(semester - 1);
            List<Grade> gradeList = semesterDetails.getGrades().get(gradesIndex);
            gradeList.addAll(utilMap.get(semester));
        }
    }

    private boolean checkGradeValidity(Grade grade) {
        return grade != null && grade.getGrade() != null && grade.getPoints() != null && grade.getGrade() != 0;
    }
}
