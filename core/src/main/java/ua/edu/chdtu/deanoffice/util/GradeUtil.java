package ua.edu.chdtu.deanoffice.util;

import ua.edu.chdtu.deanoffice.entity.Grade;

public class GradeUtil {

    public static int getGradeFromPoints(int points) {
        if (isBetween(points, 90, 100)) {
            return 5;
        }
        if (isBetween(points, 74, 89)) {
            return 4;
        }
        if (isBetween(points, 60, 73)) {
            return 3;
        }
        return 0;
    }

    private static boolean isBetween(int number, int lowerBound, int upperBound) {
        return (number >= lowerBound && number <= upperBound);
    }

    private static int getMaxPointsFromGrade(int grade) {
        switch (Math.round(grade)) {
            case 5:
                return 100;
            case 4:
                return 89;
            case 3:
                return 73;
            default:
                return 0;
        }
    }

    private static int getMinPointsFromGrade(int grade) {
        switch (Math.round(grade)) {
            case 5:
                return 90;
            case 4:
                return 74;
            case 3:
                return 60;
            default:
                return 0;
        }
    }

    public static int getAveragePointsFromGrade(Grade grade) {
        switch (Math.round(grade.getGrade())) {
            case 5:
                return 95;
            case 4:
                return 82;
            case 3:
                return 67;
            default:
                return 0;
        }
    }

    public static int[] adjustAverageGradeAndPoints(double averageGrade, double averagePoints) {
        int[] result = new int[2];
        if (Math.abs(averageGrade - 3.5) < 0.001 || Math.abs(averageGrade - 4.5) < 0.001) {
            result[1] = (int) Math.round(averagePoints);
            result[0] = GradeUtil.getGradeFromPoints(result[1]);
        } else {
            if (GradeUtil.getGradeFromPoints((int) Math.round(averagePoints)) == Math.round(averageGrade)) {
                result[0] = (int) Math.round(averageGrade);
                result[1] = (int) Math.round(averagePoints);
            }
            if (GradeUtil.getGradeFromPoints((int) Math.round(averagePoints)) > Math.round(averageGrade)) {
                result[0] = (int) Math.round(averageGrade);
                result[1] = GradeUtil.getMaxPointsFromGrade((int) Math.round(averageGrade));
            }
            if (GradeUtil.getGradeFromPoints((int) Math.round(averagePoints)) < Math.round(averageGrade)) {
                result[0] = (int) Math.round(averageGrade);
                result[1] = GradeUtil.getMinPointsFromGrade((int) Math.round(averageGrade));
            }
        }
        return result;
    }

    public static String getEctsGrade(Grade grade) {
        if (grade.getEcts() != null && grade.getCourse().getKnowledgeControl().isGraded()) {
            return grade.getEcts().toString();
        } else if (grade.getPoints() != null && grade.getPoints() >= 60) {
            return "P";
        } else {
            return "F";
        }
    }
}