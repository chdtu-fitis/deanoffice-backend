package ua.edu.chdtu.deanoffice.service.document.diploma.supplement;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.EctsGrade;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.KnowledgeControl;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.util.GradeUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class StudentSummary {

    private static Logger log = LoggerFactory.getLogger(StudentSummary.class);

    private StudentDegree studentDegree;
    private List<List<Grade>> grades;
    private Integer totalHours;
    private BigDecimal totalCredits;

    public StudentSummary(StudentDegree studentDegree, List<List<Grade>> grades) {
        this.studentDegree = studentDegree;
        this.grades = grades;
        calculateTotalHours();
        calculateTotalCredits();
        combineMultipleSemesterCourseGrades();
    }

    protected static List<Grade> getGradesByKnowledgeControlType(List<Grade> grades, Integer kcId) {
        return grades.stream().filter(grade -> grade.getCourse().getKnowledgeControl().getId() == kcId).collect(Collectors.toList());
    }

    public Student getStudent() {
        return studentDegree.getStudent();
    }

    public StudentGroup getStudentGroup() {
        return studentDegree.getStudentGroup();
    }

    private void calculateTotalHours() {
        totalHours = 0;
        grades.forEach(gradeSublist -> gradeSublist.forEach(grade -> {
            if (grade.getCourse().getHours() != null) {
                totalHours += grade.getCourse().getHours();
            }
        }));
    }

    private void combineMultipleSemesterCourseGrades() {
        List<List<Grade>> gradesToCombine = new ArrayList<>();
        GradeUtil.sortGradesByCourseNameUkr(this.grades.get(0));
        this.grades.get(0).forEach(grade -> {
            if (gradesToCombine.isEmpty()) {
                gradesToCombine.add(new ArrayList<>());
                gradesToCombine.get(0).add(grade);
            } else {
                if (gradesToCombine.get(gradesToCombine.size() - 1).stream()
                        .noneMatch(grade1 -> grade1.getCourse().getCourseName().equals(grade.getCourse().getCourseName()))) {
                    gradesToCombine.add(new ArrayList<>());
                }
                gradesToCombine.get(gradesToCombine.size() - 1).add(grade);
            }
        });

        List<Grade> combinedGrades = new ArrayList<>();
        gradesToCombine.forEach(gradesList -> combinedGrades.add(combineGrades(gradesList)));
        this.grades.get(0).clear();
        this.grades.get(0).addAll(combinedGrades);
    }

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
        }
        resultingGrade.getCourse().setHours(hoursSum);
        resultingGrade.getCourse().setCredits(new BigDecimal((double) hoursSum / resultingGrade.getCourse().getHoursPerCredit()));
        return resultingGrade;
    }

    protected Grade combineEqualGrades(List<Grade> grades) {
        Grade resultingGrade = new Grade();
        resultingGrade.setStudentDegree(grades.get(0).getStudentDegree());
        resultingGrade.setId(grades.get(0).getId());

        resultingGrade.setCourse(grades.get(0).getCourse());
        Course newCourse = new Course();
        newCourse.setHours(0);
        newCourse.setCourseName(resultingGrade.getCourse().getCourseName());
        newCourse.setKnowledgeControl(resultingGrade.getCourse().getKnowledgeControl());
        newCourse.setHoursPerCredit(resultingGrade.getCourse().getHoursPerCredit());
        resultingGrade.setCourse(newCourse);

        Double pointsSum = 0.0;
        Double gradesSum = 0.0;
        for (Grade g : grades) {
            if (g.getPoints() != null) {
                pointsSum += g.getPoints();
            }
            if (g.getGrade() != null) {
                gradesSum += g.getGrade();
            }
        }

        double averageGrade = gradesSum / (double) grades.size();
        double averagePoints = pointsSum / (double) grades.size();

        if (resultingGrade.getCourse().getKnowledgeControl().isGraded()) {
            int[] gradeAndPoints = GradeUtil.adjustAverageGradeAndPoints(averageGrade, averagePoints);
            resultingGrade.setGrade(gradeAndPoints[0]);
            resultingGrade.setPoints(gradeAndPoints[1]);
        } else {
            resultingGrade.setGrade((int) GradeUtil.roundPoints(averageGrade));
            resultingGrade.setPoints((int) GradeUtil.roundPoints(averagePoints));
            if (resultingGrade.getPoints() >= 60) {
                resultingGrade.setEcts(EctsGrade.getEctsGrade(resultingGrade.getPoints()));
            } else {
                resultingGrade.setEcts(EctsGrade.F);
            }
        }
        return resultingGrade;
    }

    private void calculateTotalCredits() {
        totalCredits = new BigDecimal(0);
        grades.forEach(gradeSublist -> gradeSublist.forEach(grade -> {
            if (grade.getCourse().getCredits() != null) {
                totalCredits = totalCredits.add(grade.getCourse().getCredits());
            }
        }));
    }

    public Double getTotalGrade() {
        int pointSum = 0;
        int pointsCount = 0;
        for (List<Grade> gradesSublist : grades) {
            for (Grade g : gradesSublist) {
                if (g.getPoints() != null && g.getCourse().getKnowledgeControl().isGraded() && g.getPoints() > 0) {
                    pointSum += g.getPoints();
                    pointsCount++;
                }
            }
        }
        try {
            return (pointSum * 1.0) / pointsCount;
        } catch (Exception e) {
            return 0.0;
        }

    }

    public String getTotalNationalGradeUkr() {
        Grade grade = new Grade();
        grade.setEcts(getTotalEcts());
        Course course = new Course();
        grade.setCourse(course);
        KnowledgeControl kc = new KnowledgeControl();
        kc.setGraded(true);
        course.setKnowledgeControl(kc);
        return grade.getNationalGradeUkr();
    }

    public String getTotalNationalGradeEng() {
        Grade grade = new Grade();
        grade.setEcts(getTotalEcts());
        Course course = new Course();
        grade.setCourse(course);
        KnowledgeControl kc = new KnowledgeControl();
        kc.setGraded(true);
        course.setKnowledgeControl(kc);
        return grade.getNationalGradeEng();
    }

    EctsGrade getTotalEcts() {
        return EctsGrade.getEctsGrade((int) GradeUtil.roundPoints(getTotalGrade()));
    }
}
