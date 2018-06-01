package ua.edu.chdtu.deanoffice.service.document.report.groupgrade;

import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.StudentSummary;

import java.math.BigDecimal;
import java.util.List;

public class StudentSummaryForGroup extends StudentSummary {


    public StudentSummaryForGroup(StudentDegree studentDegree, List<List<Grade>> grades) {
        super(studentDegree, grades);
    }

    public Double getAverageGrade() {
        double gradeSum = 0;
        double gradeCount = 0;
        for (List<Grade> gradeList : getGrades()) {
            for (Grade grade : gradeList) {
                if (grade.getCourse().getKnowledgeControl().isGraded() && grade.getGrade() != null) {
                    gradeSum += grade.getGrade();
                    gradeCount++;
                }
            }
        }
        if (gradeCount == 0) {
            return 0.0;
        } else {
            return gradeSum / gradeCount;
        }
    }

    @Override
    protected Grade combineGrades(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return null;
        }
        Grade resultingGrade;
        Integer hoursSum = 0;
        for (Grade g : grades) {
            hoursSum += g.getCourse().getHours();
        }
        if (grades.size() == 1) {
            return grades.get(0);
        } else {
            resultingGrade = combineGradeWithRightStrategy(grades);
        }
        CombinedCourse newCourse = new CombinedCourse(resultingGrade.getCourse());
        if (resultingGrade.getCourse() instanceof CombinedCourse) {
            newCourse.setCombined(((CombinedCourse) resultingGrade.getCourse()).isCombined());
        }
        newCourse.setNumberOfSemesters(grades.size());
        newCourse.setStartingSemester(grades.get(0).getCourse().getSemester());
        if (newCourse.getSemester() == null) {
            newCourse.setSemester(grades.get(0).getCourse().getSemester());
        }
        newCourse.setHours(hoursSum);
        newCourse.setCredits(new BigDecimal((double) hoursSum / newCourse.getHoursPerCredit()));
        resultingGrade.setCourse(newCourse);
        return resultingGrade;
    }

    private Grade combineGradeWithRightStrategy(List<Grade> grades) {
        Grade resultingGrade;
        List<Grade> examsGrades = getGradesByKnowledgeControlType(grades, Constants.EXAM);
        switch (examsGrades.size()) {
            case 1:
                resultingGrade = examsGrades.get(0);
                break;
            case 0:
                List<Grade> differentiatedCreditGrades = getGradesByKnowledgeControlType(grades, Constants.DIFFERENTIATED_CREDIT);
                switch (differentiatedCreditGrades.size()) {
                    case 0:
                        resultingGrade = combineEqualGrades(grades);
                        break;
                    case 1:
                        resultingGrade = differentiatedCreditGrades.get(0);
                        break;
                    default:
                        resultingGrade = combineEqualGrades(differentiatedCreditGrades);
                        break;
                }
                break;
            default:
                resultingGrade = combineEqualGrades(examsGrades);
                break;
        }
        return resultingGrade;
    }

    @Override
    protected Grade combineEqualGrades(List<Grade> grades) {
        Grade result = super.combineEqualGrades(grades);
        CombinedCourse newCourse = new CombinedCourse(result.getCourse());
        newCourse.setCombined(true);
        result.setCourse(newCourse);
        return result;

    }
}
