package ua.edu.chdtu.deanoffice.service.document.diploma.supplement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.KnowledgeControl;
import ua.edu.chdtu.deanoffice.entity.Student;

import java.math.BigDecimal;
import java.text.Collator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class StudentSummary {

    private static Logger log = LoggerFactory.getLogger(StudentSummary.class);

    private List<List<Grade>> grades;
    private Student student;

    public List<List<Grade>> getGrades() {
        return grades;
    }

    public Student getStudent() {
        return student;
    }

    public StudentSummary(Student student, List<List<Grade>> grades) {
        this.student = student;
        this.grades = new ArrayList<>();
        this.grades = grades;
        normalize();
    }

    private void normalize() {
        setHours();
        setCredits(30.0);
        setECTS();
        setGrades();
        combineMultipleSemesterCourseGrades();
    }

    private void setHours() {
        grades.forEach(gradeSublist -> {
            gradeSublist.forEach(grade -> {
                if (grade.getCourse().getHours() == null)
                    grade.getCourse().setHours(0);
            });
        });
    }

    private void setCredits(Double hoursPerCredit) {
        grades.forEach(gradeSublist -> {
            gradeSublist.forEach(grade -> {
                grade.getCourse().setCredits(new BigDecimal(grade.getCourse().getHours() / hoursPerCredit));
            });
        });
    }

    private void setECTS() {
        grades.forEach(gradeSublist -> {
            gradeSublist.forEach(grade -> {
                grade.setEcts(getECTSGrade(grade.getPoints()));
            });

        });
    }

    public static String getECTSGrade(long points) {
        if (points >= 90 && points <= 100) return "A";
        if (points >= 82 && points <= 89) return "B";
        if (points >= 74 && points <= 81) return "C";
        if (points >= 64 && points <= 73) return "D";
        if (points >= 60 && points <= 63) return "E";
        if (points >= 35 && points <= 59) return "Fx";
        if (points >= 0 && points <= 34) return "F";
        else return "";
    }


    private void setGrades() {
        grades.forEach(gradeSublist -> {
            gradeSublist.forEach(grade -> {
                grade.setGrade(getGradeFromPoints(grade.getPoints()));
            });
        });
    }

    public static int[] adjustAverageGradeAndPoints(double averageGrade, double averagePoints) {
        int[] result = new int[2];
        if (Math.abs(averageGrade - 3.5) < 0.001 || Math.abs(averageGrade - 4.5) < 0.001) {
            result[1] = (int) Math.round(averagePoints);
            result[0] = getGradeFromPoints(result[1]);
        } else {
            if (getGradeFromPoints(averagePoints) == Math.round(averageGrade)) {
                result[0] = (int) Math.round(averageGrade);
                result[1] = (int) Math.round(averagePoints);
            }
            if (getGradeFromPoints(averagePoints) > Math.round(averageGrade)) {
                result[0] = (int) Math.round(averageGrade);
                result[1] = getMaxPointsFromGrade(averageGrade);
            }
            if (getGradeFromPoints(averagePoints) < Math.round(averageGrade)) {
                result[0] = (int) Math.round(averageGrade);
                result[1] = getMinPointsFromGrade(averageGrade);
            }
        }
        return result;
    }

    public static int getGradeFromPoints(double points) {
        if (points >= 90 && points <= 100) return 5;
        if (points >= 74 && points <= 89) return 4;
        if (points >= 60 && points <= 73) return 3;
        return 0;
    }

    public static int getMaxPointsFromGrade(double grade) {
        if (Math.round(grade) == 5) return 100;//impossible situation;
        if (Math.round(grade) == 4) return 89;
        if (Math.round(grade) == 3) return 73;
        return 0;
    }

    public static int getMinPointsFromGrade(double grade) {
        if (Math.round(grade) == 5) return 90;
        if (Math.round(grade) == 4) return 74;
        if (Math.round(grade) == 3) return 60;//impossible situation;
        return 0;
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
                        .noneMatch(grade1 -> grade1.getCourse().getCourseName().equals(grade.getCourse().getCourseName())))
                    gradesToCombine.add(new ArrayList<>());
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
        if (grades == null || grades.isEmpty())
            return null;
        if (grades.size() == 1)
            return grades.get(0);
        else {
            List<Grade> examsGrades = getExamGrades(grades);
            if (examsGrades.size() == 0) {
                List<Grade> differentiatedCreditGrades = getDifferentiatedCreditsGrades(grades);
                if (examsGrades.size() == 0)
                    return combineEqualPriorityGrades(grades);
                if (differentiatedCreditGrades.size() == 1)
                    return differentiatedCreditGrades.get(0);
                else {
                    return combineEqualPriorityGrades(differentiatedCreditGrades);
                }
            }
            if (examsGrades.size() == 1)
                return examsGrades.get(0);
            else {
                return combineEqualPriorityGrades(examsGrades);
            }
        }
    }

    private Grade combineEqualPriorityGrades(List<Grade> grades) {
        Grade resultingGrade = grades.get(0);
        Double pointsSum = 0.0;
        Double gradesSum = 0.0;
        for (Grade g : grades) {
            pointsSum += g.getPoints();
            gradesSum += g.getGrade();
        }
        int[] pointsAndGrade = adjustAverageGradeAndPoints(
                gradesSum / grades.size(),
                pointsSum / grades.size());
        resultingGrade.setPoints(pointsAndGrade[1]);
        resultingGrade.setGrade(pointsAndGrade[0]);
        return resultingGrade;
    }

    private List<Grade> getExamGrades(List<Grade> grades) {
        return grades.stream().filter(grade -> grade.getCourse().getKnowledgeControl().getName().equals("іспит")).collect(Collectors.toList());
    }

    private List<Grade> getDifferentiatedCreditsGrades(List<Grade> grades) {
        return grades.stream().filter(grade -> grade.getCourse().getKnowledgeControl().getName().equals("диференційований залік")).collect(Collectors.toList());
    }

    public static String getNationalGradeUkr(Grade g) {
        if (g.getCourse().getKnowledgeControl().isHasGrade()) {
            switch (g.getEcts()) {
                case "A":
                    return "Відмінно";
                case "B":
                    return "Добре";
                case "C":
                    return "Добре";
                case "D":
                    return "Задовільно";
                case "E":
                    return "Задовільно";
                case "F":
                    return "Незадовільно";
            }
        } else if (g.getEcts().equals("F") || g.getEcts().equals("Fx")) {
            return "Не зараховано";
        } else
            return "Зараховано";
        return "";
    }

    public static String getNationalGradeEng(Grade g) {
        if (g.getCourse().getKnowledgeControl().isHasGrade()) {
            switch (g.getEcts()) {
                case "A":
                    return "Excellent";
                case "B":
                    return "Good";
                case "C":
                    return "Good";
                case "D":
                    return "Satisfactory";
                case "E":
                    return "Satisfactory";
                case "F":
                    return "Fail";
            }
        } else if (g.getEcts().equals("F") || g.getEcts().equals("Fx")) {
            return "Fail";
        } else
            return "Passed";
        return "";
    }

    public static Map<String, String> getGradeDictionary(Grade grade) {
        Map<String, String> result = new HashMap<>();
        try {
            result.put("#CourseNameUkr", grade.getCourse().getCourseName().getName());
            result.put("#CourseNameEng", grade.getCourse().getCourseName().getNameEng());
            result.put("#Credits", String.format("%.1f", grade.getCourse().getCredits()));
            result.put("#Hours", String.format("%d", grade.getCourse().getHours()));
            result.put("#LocalGrade", String.format("%d", grade.getPoints()));
            result.put("#NationalGradeUkr", getNationalGradeUkr(grade));
            result.put("#NationalGradeEng", getNationalGradeEng(grade));
            result.put("#ECTSGrade", grade.getEcts());
        } catch (NullPointerException e) {
            log.warn("Some of grade's properties are null!");
        }
        return result;
    }

    public Map<String, String> getTotalDictionary() {
        Map<String, String> result = new HashMap<>();
            result.put("#TotalHours", String.format("%4d", getTotalHours()));
            result.put("#TotalCredits", String.format("%2.1f", getTotalCredits(30.0)));
            result.put("#TotalGrade", String.format("%2d", Math.round(getTotalGrade())));
            result.put("#TotalNGradeUkr", getTotalNationalGradeUkr());
            result.put("#TotalNGradeEng", getTotalNationalGradeEng());
            result.put("#TotalECTS", getTotalECTS());
        return result;
    }

    public Map<String, String> getStudentInfoDictionary() {
        Map<String, String> result = new HashMap<>();

        result.put("#SurnameUkr", student.getSurname());
        result.put("#SurnameEng", student.getSurnameEng());
        result.put("#NameUkr", student.getName());
        result.put("#NameEng", student.getNameEng());
        result.put("#PatronimicUkr", student.getPatronimic());

        DateFormat dateOfBirthFormat = new SimpleDateFormat("dd.MM.yyyy");
        result.put("#BirthDate",
                student.getBirthDate() != null
                        ? dateOfBirthFormat.format(student.getBirthDate())
                        : "#BirthDate");

        String modeOfStudyUkr = "";
        String modeOfStudyEng = "";
        char modeOfStudy = student.getStudentGroup().getTuitionForm();
        if (modeOfStudy == 'f') {
            modeOfStudyUkr = "Денна";
            modeOfStudyEng = "Full-time";
        } else if (modeOfStudy == 's') {
            modeOfStudyUkr = "Заочна";
            modeOfStudyEng = "Extramural";
        }
        result.put("#ModeOfStudyUkr", modeOfStudyUkr);
        result.put("#ModeOfStudyEng", modeOfStudyEng);

        result.put("#SpecializationUkr", student.getStudentGroup().getSpecialization().getName());
        result.put("#SpecializationEng", student.getStudentGroup().getSpecialization().getNameEng());
        result.put("#SpecialityUkr", student.getStudentGroup().getSpecialization().getSpeciality().getName());
        result.put("#SpecialityEng", student.getStudentGroup().getSpecialization().getSpeciality().getNameEng());
        result.put("#DegreeUkr", student.getStudentGroup().getSpecialization().getDegree().getName());
        result.put("#DegreeEng", student.getStudentGroup().getSpecialization().getDegree().getNameEng());
        try {
            result.put("#QualificationUkrP1", student.getStudentGroup().getSpecialization().getQualification().split("  ")[0]);
            result.put("#QualificationEngP1", student.getStudentGroup().getSpecialization().getQualificationEng().split("  ")[0]);
            result.put("#QualificationUkrP2", student.getStudentGroup().getSpecialization().getQualification().split("  ")[1]);
            result.put("#QualificationEngP2", student.getStudentGroup().getSpecialization().getQualificationEng().split("  ")[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            log.warn("Qualification should consist of 2 parts, divided with double space");
        }
        return result;
    }

    public int getTotalHours() {
        int result = 0;
        for (List<Grade> gradesSublist :
                grades) {
            for (Grade g : gradesSublist) {
                result += g.getCourse().getHours();
            }
        }
        return result;
    }

    public double getTotalCredits(double hoursPerCredit) {
        return getTotalHours() / hoursPerCredit;
    }

    public Double getTotalGrade() {
        int pointSum = 0;
        int pointsCount = 0;
        for (List<Grade> gradesSublist :
                grades) {
            for (Grade g : gradesSublist) {
                if (g.getPoints() > 0) {
                    pointSum += g.getPoints();
                    pointsCount++;
                }
            }
        }
        if (pointsCount == 0)
            pointsCount = 1;
        return (pointSum * 1.0) / pointsCount;
    }

    public String getTotalNationalGradeUkr() {
        Grade g = new Grade();
        g.setEcts(getTotalECTS());
        Course c = new Course();
        g.setCourse(c);
        KnowledgeControl kc = new KnowledgeControl();
        kc.setHasGrade(true);
        c.setKnowledgeControl(kc);
        return getNationalGradeUkr(g);
    }

    public String getTotalNationalGradeEng() {
        Grade g = new Grade();
        g.setEcts(getTotalECTS());
        Course c = new Course();
        g.setCourse(c);
        KnowledgeControl kc = new KnowledgeControl();
        kc.setHasGrade(true);
        c.setKnowledgeControl(kc);
        return getNationalGradeEng(g);
    }

    private String getTotalECTS() {
        return getECTSGrade(Math.round(getTotalGrade()));
    }

}
