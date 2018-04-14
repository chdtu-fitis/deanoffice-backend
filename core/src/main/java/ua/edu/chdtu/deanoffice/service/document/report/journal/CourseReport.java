package ua.edu.chdtu.deanoffice.service.document.report.journal;

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
    private String course;
    private String hours;
    private String teacher;
    private String date;

    CourseReport(String course, String hours, String teacher, String date) {
        this.course = course;
        this.hours = hours;
        this.teacher = teacher;
        this.date = date;
    }

    Map<String, String> getDictionary() {
        Map<String, String> result = new HashMap<>();
        result.put("Pred", course);
        result.put("H", hours);
        result.put("V", teacher);
        result.put("D", date);
        return result;
    }
}
