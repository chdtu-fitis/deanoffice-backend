package ua.edu.chdtu.deanoffice.api.diplomasupplement;

import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.KnowledgeControl;
import ua.edu.chdtu.deanoffice.entity.Student;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class StudentSummary {

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
        Collections.copy(this.grades, grades);
        normalize();
    }

    private void normalize() {
        setHours();
        setCredits(30.0);
        setECTS();
        setGrades();
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

    private static String getECTSGrade(long points) {
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

    public static double[] adjustAverageGradeAndPoints(double averageGradeScale, double averagePoints) {
        double[] result = new double[2];
        if (Math.abs(averageGradeScale - 3.5) < 0.001 || Math.abs(averageGradeScale - 4.5) < 0.001) {
            result[1] = (int) Math.round(averagePoints);
            result[0] = getGradeFromPoints(result[1]);
        } else {
            if (getGradeFromPoints(averagePoints) == Math.round(averageGradeScale)) {
                result[0] = (int) Math.round(averageGradeScale);
                result[1] = (int) Math.round(averagePoints);
            }
            if (getGradeFromPoints(averagePoints) > Math.round(averageGradeScale)) {
                result[0] = (int) Math.round(averageGradeScale);
                result[1] = getMaxPointsFromGrade(averageGradeScale);
            }
            if (getGradeFromPoints(averagePoints) < Math.round(averageGradeScale)) {
                result[0] = (int) Math.round(averageGradeScale);
                result[1] = getMinPointsFromGrade(averageGradeScale);
            }
        }
        return result;
    }

    private static int getGradeFromPoints(double points) {
        if (points >= 90 && points <= 100) return 5;
        if (points >= 74 && points <= 89) return 4;
        if (points >= 60 && points <= 73) return 3;
        return 0;
    }

    private static int getMaxPointsFromGrade(double gradeScale) {
        if (Math.round(gradeScale) == 5) return 100;//impossible situation;
        if (Math.round(gradeScale) == 4) return 89;
        if (Math.round(gradeScale) == 3) return 73;
        return 0;
    }

    private static int getMinPointsFromGrade(double gradeScale) {
        if (Math.round(gradeScale) == 5) return 90;
        if (Math.round(gradeScale) == 4) return 74;
        if (Math.round(gradeScale) == 3) return 60;//impossible situation;
        return 0;
    }

    private static String getNationalGradeUkr(Grade g) {
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

    private static String getNationalGradeEng(Grade g) {
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

    private static Map<String, String> getGradeDictionary(Grade grade) {
        Map<String, String> result = new HashMap<>();
        try {
            result.put("#UkrSubjName", grade.getCourse().getCourseName().getName());
            result.put("#EngSubjName", grade.getCourse().getCourseName().getNameEng());
            result.put("#Credits", String.format("%.1f", grade.getCourse().getCredits()));
            result.put("#Hours", String.format("%d", grade.getCourse().getHours()));
            result.put("#Points", String.format("%.0d", grade.getPoints()));
            result.put("#UkrNatGrade", getNationalGradeUkr(grade));
            result.put("#EngNatGrade", getNationalGradeEng(grade));
            result.put("#ECTS", grade.getEcts());
        } catch (NullPointerException e) {
            //Debug
            //System.out.println("Some of the grade's properties are null! " + this.getCourseName());
        }
        return result;
    }

    public Map<String, String> getTotalDictionary() {
        Map<String, String> result = new HashMap<>();
        try {
            result.put("#TotalHours", String.format("%4d", getTotalHours()));
            result.put("#TotalCredits", String.format("%2.1f", getTotalCredits(30.0)));
            result.put("#TotalGrade", String.format("%2d", Math.round(getTotalGrade())));
            result.put("#TotalNGradeUkr", getTotalNationalGradeUkr());
            result.put("#TotalNGradeEng", getTotalNationalGradeEng());
            result.put("#TotalECTS", getTotalECTS());
        } catch (NullPointerException e) {
            //Should not happen
        }
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
        result.put("#QualificationUkrP1", student.getStudentGroup().getSpecialization().getQualification().split("  ")[0]);
        result.put("#QualificationEngP1", student.getStudentGroup().getSpecialization().getQualificationEng().split("  ")[0]);
        result.put("#QualificationUkrP2", student.getStudentGroup().getSpecialization().getQualification().split("  ")[1]);
        result.put("#QualificationEngP2", student.getStudentGroup().getSpecialization().getQualificationEng().split("  ")[1]);
        return result;
    }

    private int getTotalHours() {
        int result = 0;
        for (List<Grade> gradesSublist :
                grades) {
            for (Grade g : gradesSublist) {
                result += g.getCourse().getHours();
            }
        }
        return result;
    }

    private double getTotalCredits(double hoursPerCredit) {
        return getTotalHours() / hoursPerCredit;
    }

    private Double getTotalGrade() {
        int pointSum = 0;
        //1 is to avoid division by zero
        int pointsCount = 1;
        for (List<Grade> gradesSublist :
                grades) {
            for (Grade g : gradesSublist) {
                if (g.getPoints() > 0) {
                    pointSum += g.getPoints();
                    pointsCount++;
                }
            }
        }
        return (pointSum * 1.0) / (pointsCount - 1);
    }

    private String getTotalNationalGradeUkr() {
        Grade g = new Grade();
        g.setEcts(getTotalECTS());
        Course c = new Course();
        g.setCourse(c);
        KnowledgeControl kc = new KnowledgeControl();
        kc.setHasGrade(true);
        c.setKnowledgeControl(kc);
        return getNationalGradeUkr(g);
    }

    private String getTotalNationalGradeEng() {
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
