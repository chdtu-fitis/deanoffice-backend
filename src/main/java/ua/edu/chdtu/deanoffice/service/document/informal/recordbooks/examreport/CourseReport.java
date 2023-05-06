package ua.edu.chdtu.deanoffice.service.document.informal.recordbooks.examreport;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
class CourseReport {

    private static Logger log = LoggerFactory.getLogger(CourseReport.class);
    private String number;
    private String course;
    private String hours;
    private String knowledgeControl;
    private String teacher;
    private String date;

    CourseReport(String number, String course, String hours, String knowledgeControl, String teacher, String date) {
        this.number = number;
        this.course = course;
        this.hours = hours;
        this.knowledgeControl = knowledgeControl;
        this.teacher = teacher;
        this.date = date;
    }

    Map<String, String> getDictionary() {
        Map<String, String> result = new HashMap<>();
        result.put("N", number);
        result.put("Pred", course);
        result.put("H", hours);
        result.put("KC", knowledgeControl);
        result.put("V", teacher);
        result.put("D", date);
        return result;
    }
}
