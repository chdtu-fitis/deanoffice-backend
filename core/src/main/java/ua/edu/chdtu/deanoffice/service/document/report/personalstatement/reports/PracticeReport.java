package ua.edu.chdtu.deanoffice.service.document.report.personalstatement.reports;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class PracticeReport {
    private static Logger log = LoggerFactory.getLogger(PracticeReport.class);
    private int number;
    private String name;
    private int score;
    private int point;
    private String eCTS;

    public PracticeReport(String name, int number,
                          int score, int point, String eCTS) {
        this.name = name;
        this.number = number;
        this.score = score;
        this.point = point;
        this.eCTS = eCTS;
    }

    public Map<String, String> getDictionary() {
        Map<String, String> result = new HashMap<>();
        result.put("Num", String.valueOf(number));
        result.put("PracticeName", name);
        result.put("Score", String.valueOf(score));
        result.put("Point", String.valueOf(point));
        result.put("ECTS", eCTS);
        return result;
    }
}
