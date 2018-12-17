package ua.edu.chdtu.deanoffice.util;

import ua.edu.chdtu.deanoffice.entity.Grade;

import java.text.Collator;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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

    public static boolean isEnoughToPass(Integer points) {
        return (points == null || points < 60) ? false : true;
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
            result[1] = (int) roundPoints(averagePoints);
            result[0] = GradeUtil.getGradeFromPoints(result[1]);
        } else {
            if (GradeUtil.getGradeFromPoints((int) roundPoints(averagePoints)) == Math.round(averageGrade)) {
                result[0] = (int) Math.round(averageGrade);
                result[1] = (int) roundPoints(averagePoints);
            }
            if (GradeUtil.getGradeFromPoints((int) roundPoints(averagePoints)) > Math.round(averageGrade)) {
                result[0] = (int) Math.round(averageGrade);
                result[1] = GradeUtil.getMaxPointsFromGrade((int) Math.round(averageGrade));
            }
            if (GradeUtil.getGradeFromPoints((int) roundPoints(averagePoints)) < Math.round(averageGrade)) {
                result[0] = (int) Math.round(averageGrade);
                result[1] = GradeUtil.getMinPointsFromGrade((int) Math.round(averageGrade));
            }
        }
        return result;
    }

    public static long roundPoints(double points) {
        NumberFormat format = DecimalFormat.getInstance();
        DecimalFormatSymbols symbols = ((DecimalFormat) format).getDecimalFormatSymbols();
        char separator = symbols.getDecimalSeparator();

        double decimalPart = Double.parseDouble("0." + String.format("%.5f", points).split(String.valueOf(separator))[1]);
        double precision = 0.001;
        if (Math.abs(decimalPart - 0.5) < precision) {
            points += precision;
        }
        return Math.round(points);
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

    public static void sortGradesByCourseNameUkr(List<Grade> grades) {
        grades.sort((o1, o2) -> {
            Collator ukrainianCollator = Collator.getInstance(new Locale("uk", "UA"));
            return ukrainianCollator.compare(o1.getCourse().getCourseName().getName(), o2.getCourse().getCourseName().getName());
        });
    }
}