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
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Getter
public class StudentSummary {

    private static Logger log = LoggerFactory.getLogger(StudentSummary.class);

    private StudentDegree studentDegree;
    private List<List<Grade>> grades;
    private Integer totalHours = 0;

    public StudentSummary(StudentDegree studentDegree, List<List<Grade>> grades) {
        this.studentDegree = studentDegree;
        this.grades = grades;
        completeGrades();
    }

    public Student getStudent() {
        return studentDegree.getStudent();
    }

    public StudentGroup getStudentGroup() {
        return studentDegree.getStudentGroup();
    }

    private static boolean ectsIsSet(Grade grade) {
        return grade.getEcts() != null;
    }

    private void completeGrades() {
        setHours();
        setCredits();
        setGrades();
        setPoints();
        setEcts();
        combineMultipleSemesterCourseGrades();
    }

    private void setCredits() {
        grades.forEach(gradeSublist -> {
            gradeSublist.forEach(grade -> {
                grade.getCourse().setCredits(new BigDecimal(grade.getCourse().getHours() / Constants.HOURS_PER_CREDIT));
            });
        });
    }

    private void setHours() {
        grades.forEach(gradeSublist -> {
            gradeSublist.forEach(grade -> {
                if (grade.getCourse().getHours() == null) {
                    grade.getCourse().setHours(0);
                }
                totalHours += grade.getCourse().getHours();
            });
        });
    }

    private void setGrades() {
        grades.forEach(gradeSublist -> {
            gradeSublist.forEach(grade -> {
                if (grade.getGrade() == 0 && grade.getCourse().getKnowledgeControl().getId() != Constants.CREDIT) {
                    grade.setGrade(GradeUtil.getGradeFromPoints(grade.getPoints()));
                }
            });
        });
    }

    private void setPoints() {
        grades.forEach(gradeSublist -> {
            gradeSublist.forEach(grade -> {
                if (grade.getPoints() == 0) {
                    grade.setPoints(GradeUtil.getAveragePointsFromGrade(grade));
                }
            });
        });
    }

    private void setEcts() {
        grades.forEach(gradeSublist -> {
            gradeSublist.forEach(grade -> {
                if (!ectsIsSet(grade)) {
                    grade.setEcts(EctsGrade.getEctsGrade(grade.getPoints()));
                }
            });
        });
    }

    private void combineMultipleSemesterCourseGrades() {
        List<List<Grade>> gradesToCombine = new ArrayList<>();
        sortGradesByCourseNameUkr(this.grades.get(0));
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

    private void sortGradesByCourseNameUkr(List<Grade> grades) {
        grades.sort((o1, o2) -> {
            Collator ukrainianCollator = Collator.getInstance(new Locale("uk", "UA"));
            return ukrainianCollator.compare(o1.getCourse().getCourseName().getName(), o2.getCourse().getCourseName().getName());
        });
    }

    private Grade combineGrades(List<Grade> grades) {
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
        resultingGrade.getCourse().setCredits(new BigDecimal(hoursSum / Constants.HOURS_PER_CREDIT));
        return resultingGrade;
    }

    private Grade combineEqualGrades(List<Grade> grades) {
        Grade resultingGrade = grades.get(0);
        Double pointsSum = 0.0;
        Double gradesSum = 0.0;

        for (Grade g : grades) {
            pointsSum += g.getPoints();
            gradesSum += g.getGrade();
        }

        Course newCourse = new Course();
        newCourse.setHours(0);
        newCourse.setCourseName(resultingGrade.getCourse().getCourseName());
        newCourse.setKnowledgeControl(resultingGrade.getCourse().getKnowledgeControl());
        resultingGrade.setCourse(newCourse);

        if (!resultingGrade.getCourse().getKnowledgeControl().isHasGrade()) {
            int[] pointsAndGrade = GradeUtil.adjustAverageGradeAndPoints(
                    gradesSum / grades.size(),
                    pointsSum / grades.size());
            resultingGrade.setPoints(pointsAndGrade[1]);
            resultingGrade.setGrade(pointsAndGrade[0]);
            resultingGrade.setEcts(EctsGrade.getEctsGrade(resultingGrade.getPoints()));
        } else {
            resultingGrade.setPoints((int) Math.round(pointsSum / grades.size()));
            resultingGrade.setGrade((int) Math.round(gradesSum / grades.size()));
            if (resultingGrade.getPoints() >= 60) {
                resultingGrade.setEcts(EctsGrade.getEctsGrade(resultingGrade.getPoints()));
            } else {
                resultingGrade.setEcts(EctsGrade.F);
            }
        }
        return resultingGrade;
    }

    private static List<Grade> getGradesByKnowledgeControlType(List<Grade> grades, Integer kcId) {
        return grades.stream().filter(grade -> grade.getCourse().getKnowledgeControl().getId() == kcId).collect(Collectors.toList());
    }

    public BigDecimal getTotalCredits() {
        return new BigDecimal(getTotalHours() / Constants.HOURS_PER_CREDIT);
    }


    public Double getTotalGrade() {
        int pointSum = 0;
        int pointsCount = 0;
        for (List<Grade> gradesSublist :
                grades) {
            for (Grade g : gradesSublist) {
                if (g.getCourse().getKnowledgeControl().isHasGrade() && g.getPoints() > 0) {
                    pointSum += g.getPoints();
                    pointsCount++;
                }
            }
        }
        if (pointsCount == 0) {
            pointsCount = 1;
        }
        return (pointSum * 1.0) / pointsCount;
    }

    public String getTotalNationalGradeUkr() {
        Grade grade = new Grade();
        grade.setEcts(getTotalEcts());
        Course course = new Course();
        grade.setCourse(course);
        KnowledgeControl kc = new KnowledgeControl();
        kc.setHasGrade(true);
        course.setKnowledgeControl(kc);
        return grade.getNationalGradeUkr();
    }

    public String getTotalNationalGradeEng() {
        Grade grade = new Grade();
        grade.setEcts(getTotalEcts());
        Course course = new Course();
        grade.setCourse(course);
        KnowledgeControl kc = new KnowledgeControl();
        kc.setHasGrade(true);
        course.setKnowledgeControl(kc);
        return grade.getNationalGradeEng();
    }

    EctsGrade getTotalEcts() {
        return EctsGrade.getEctsGrade((int) Math.round(getTotalGrade()));
    }
}
