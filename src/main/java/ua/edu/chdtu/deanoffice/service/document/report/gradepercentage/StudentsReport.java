package ua.edu.chdtu.deanoffice.service.document.report.gradepercentage;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.chdtu.deanoffice.entity.EctsGrade;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class StudentsReport {

    private static Logger log = LoggerFactory.getLogger(StudentsReport.class);

    private StudentDegree studentDegree;

    private String satisfactoryPercentage;
    private String goodPercentage;
    private String excellentPercentage;

    private String aPercentage;
    private String bPercentage;
    private String cPercentage;
    private String dPercentage;
    private String ePercentage;

    public StudentsReport(StudentDegree studentDegree, @NonNull List<Grade> grades) {
        this.studentDegree = studentDegree;
        int quantity = grades.size();
        int badGrades = 0;

        double satisfactory = 0;
        double good = 0;
        double excellent = 0;

        double aSum = 0;
        double bSum = 0;
        double cSum = 0;
        double dSum = 0;
        double eSum = 0;

        for (Grade grade : grades) {
            if (grade.getGrade() != null) {
                switch (grade.getGrade()) {
                    case 3: {
                        satisfactory++;
                        if (grade.getPoints() != null && EctsGrade.getEctsGrade(grade.getPoints()).equals(EctsGrade.D)) {
                            dSum++;
                        } else {
                            eSum++;
                        }
                        break;
                    }
                    case 4: {
                        good++;
                        if (grade.getPoints() != null && EctsGrade.getEctsGrade(grade.getPoints()).equals(EctsGrade.B)) {
                            bSum++;
                        } else {
                            cSum++;
                        }
                        break;
                    }
                    case 5: {
                        excellent++;
                        aSum++;
                        break;
                    }
                    default: {
                        badGrades++;
                    }
                }
            } else {
                badGrades++;
            }
        }

        if (badGrades > 0) {
            log.warn("Report for student " + studentDegree.getStudent().getInitialsUkr() + " ignores " +
                    String.format("%2d", badGrades) +
                    " grade(s) where grade is lower than satisfactory!");
        }
        satisfactoryPercentage = String.format("%5.2f", satisfactory / (quantity - badGrades) * 100.0);
        goodPercentage = String.format("%5.2f", good / (quantity - badGrades) * 100.0);
        excellentPercentage = String.format("%5.2f", excellent / (quantity - badGrades) * 100.0);

        aPercentage = String.format("%5.2f", aSum / (quantity - badGrades) * 100.0);
        bPercentage = String.format("%5.2f", bSum / (quantity - badGrades) * 100.0);
        cPercentage = String.format("%5.2f", cSum / (quantity - badGrades) * 100.0);
        dPercentage = String.format("%5.2f", dSum / (quantity - badGrades) * 100.0);
        ePercentage = String.format("%5.2f", eSum / (quantity - badGrades) * 100.0);

    }

    public Map<String, String> getDictionary() {
        Map<String, String> result = new HashMap<>();
        result.put("Initials", studentDegree.getStudent().getInitialsUkr());
        result.put("Satisfactory", satisfactoryPercentage);
        result.put("Good", goodPercentage);
        result.put("Excellent", excellentPercentage);
        result.put("A", aPercentage);
        result.put("B", bPercentage);
        result.put("C", cPercentage);
        result.put("D", dPercentage);
        result.put("E", ePercentage);
        return result;
    }
}
