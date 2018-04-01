package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;

public enum EctsGrade {
    A(1, "Відмінно", "Excellent", 90, 100),
    B(2, "Добре", "Good", 82, 89),
    C(3, "Добре", "Good", 74, 81),
    D(4, "Задовільно", "Satisfactory", 64, 73),
    E(5, "Задовільно", "Satisfactory", 60, 63),
    FX(6, "Незадовільно", "Fail", 35, 59),
    F(7, "Незадовільно", "Fail", 0, 34);

    private final int id;
    private final String nameUkr;
    private final String nameEng;
    @Getter
    private final int lowerBound;
    @Getter
    private final int upperBound;

    EctsGrade(int id, String nameUkr, String nameEng, int lowerBound, int upperBound) {
        this.id = id;
        this.nameUkr = nameUkr;
        this.nameEng = nameEng;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public String getNationalGradeUkr(Grade grade) {
        if (!grade.getCourse().getKnowledgeControl().isHasGrade()) {
            if (grade.getEcts().equals(F) || grade.getEcts().equals(FX)) {
                return "Не зараховано";
            } else {
                return "Зараховано";
            }
        } else {
            return grade.getEcts().nameUkr;
        }
    }

    public String getNationalGradeEng(Grade grade) {
        if (!grade.getCourse().getKnowledgeControl().isHasGrade()) {
            if (grade.getEcts().equals(F) || grade.getEcts().equals(FX)) {
                return "Fail";
            } else {
                return "Passed";
            }
        } else {
            return grade.getEcts().nameEng;
        }
    }

    private static boolean isBetween(int number, int lowerBound, int upperBound) {
        return (number >= lowerBound && number <= upperBound);
    }

    public static EctsGrade getEctsGrade(int points) {
        for (EctsGrade ectsGrade : EctsGrade.values()) {
            if (isBetween(points, ectsGrade.lowerBound, ectsGrade.upperBound)) {
                return ectsGrade;
            }
        }
        return null;
    }
}
