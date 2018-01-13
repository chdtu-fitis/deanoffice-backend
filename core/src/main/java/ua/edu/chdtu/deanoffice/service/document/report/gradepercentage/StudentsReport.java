package ua.edu.chdtu.deanoffice.service.document.report.gradepercentage;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.util.GradeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class StudentsReport {

    private static Logger log = LoggerFactory.getLogger(StudentsReport.class);

    private Student student;

    private String satisfactoryPercentage;
    private String goodPercentage;
    private String excellentPercentage;

    private String APercentage;
    private String BPercentage;
    private String CPercentage;
    private String DPercentage;
    private String EPercentage;

    public StudentsReport(@NonNull List<Grade> grades) {
        student = grades.get(0).getStudent();
        int quantity = grades.size();
        int badGrades = 0;

        double satisfactory = 0;
        double good = 0;
        double excellent = 0;

        double a = 0;
        double b = 0;
        double c = 0;
        double d = 0;
        double e = 0;

        for (Grade grade : grades) {
            switch (grade.getGrade()) {
                case 3: {
                    satisfactory++;
                    if (GradeUtil.getECTSGrade(grade.getPoints()).equals("D")) {
                        d++;
                    } else e++;
                    break;
                }
                case 4: {
                    good++;
                    if (GradeUtil.getECTSGrade(grade.getPoints()).equals("B")) {
                        b++;
                    } else c++;
                    break;
                }
                case 5: {
                    excellent++;
                    a++;
                    break;
                }
                default: {
                    badGrades++;

                }
            }
        }

        if (badGrades > 0) {
            log.warn("Report for student " + student.getInitialsUkr() + " ignores " +
                    String.format("%2d", badGrades) +
                    " grade(s) where grade is lower than satisfactory!");
        }
        satisfactoryPercentage = String.format("%5.2f", satisfactory / (quantity - badGrades) * 100.0);
        goodPercentage = String.format("%5.2f", good / (quantity - badGrades) * 100.0);
        excellentPercentage = String.format("%5.2f", excellent / (quantity - badGrades) * 100.0);

        APercentage = String.format("%5.2f", a / (quantity - badGrades) * 100.0);
        BPercentage = String.format("%5.2f", b / (quantity - badGrades) * 100.0);
        CPercentage = String.format("%5.2f", c / (quantity - badGrades) * 100.0);
        DPercentage = String.format("%5.2f", d / (quantity - badGrades) * 100.0);
        EPercentage = String.format("%5.2f", e / (quantity - badGrades) * 100.0);

    }

    public Map<String, String> getDictionary() {
        Map<String, String> result = new HashMap<>();
        result.put("#Initials", student.getInitialsUkr());
        result.put("#Satisfactory", satisfactoryPercentage);
        result.put("#Good", goodPercentage);
        result.put("#Excellent", excellentPercentage);
        result.put("#A", APercentage);
        result.put("#B", BPercentage);
        result.put("#C", CPercentage);
        result.put("#D", DPercentage);
        result.put("#E", EPercentage);
        return result;
    }
}
