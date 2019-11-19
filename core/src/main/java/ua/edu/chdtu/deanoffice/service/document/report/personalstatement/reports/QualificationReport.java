package ua.edu.chdtu.deanoffice.service.document.report.personalstatement.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class QualificationReport {
    private static Logger log = LoggerFactory.getLogger(QualificationReport.class);
    private int number;
    private String name;
    private int score;
    private int point;
    private String eCTS;

    public QualificationReport(String name, int number,
                               int score, int point, String eCTS) {
        this.number = number;
        this.name = name;
        this.score = score;
        this.point = point;
        this.eCTS = eCTS;
    }

    public Map<String, String> getDictionary() {
        Map<String, String> result = new HashMap<>();
        result.put("Num", String.valueOf(number));
        result.put("Qualification", name);
        result.put("Score", String.valueOf(score));
        result.put("Point", String.valueOf(point));
        result.put("ECTS", eCTS);
        return result;
    }
}
