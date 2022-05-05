package ua.edu.chdtu.deanoffice.service.document.informal.gradesabstract.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class QualificationReport {
    private static Logger log = LoggerFactory.getLogger(QualificationReport.class);
    private int number;
    private String name;
    private String score;
    private String point;
    private String eCTS;

    public QualificationReport(String name, int number,
                               String score, String point, String eCTS) {
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
        result.put("Score", score);
        result.put("Point", point);
        result.put("ECTS", eCTS);
        return result;
    }
}
