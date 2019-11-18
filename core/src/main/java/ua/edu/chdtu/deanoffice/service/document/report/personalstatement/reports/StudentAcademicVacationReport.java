package ua.edu.chdtu.deanoffice.service.document.report.personalstatement.reports;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class StudentAcademicVacationReport {

    private static Logger log = LoggerFactory.getLogger(StudentAcademicVacationReport.class);
    private String course;
    private String number;
    private String date;
    private String content;

    public StudentAcademicVacationReport(String course, String number, String date, String content) {
        this.course = course;
        this.number = number;
        this.date = date;
        this.content = content;
    }

    public Map<String, String> getDictionary() {
        Map<String, String> result = new HashMap<>();
        result.put("Course", course);
        result.put("NumDate", number + " " + date);
        result.put("Content", content);
        return result;
    }
}
