package ua.edu.chdtu.deanoffice.service.document.report.journal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GroupList {
    private static Logger log = LoggerFactory.getLogger(CourseReport.class);
    private String groupName;
    private String semester;
    private String course;

    GroupList(String groupName, String semester, String course) {
        this.groupName = groupName;
        this.semester = semester;
        this.course = course;
    }

    Map<String, String> getDictionaryForGroupName() {
        Map<String, String> result = new HashMap<>();
        result.put("GroupName", groupName);
        return result;
    }

    Map<String, String> getDictionaryForSemester() {
        Map<String, String> result = new HashMap<>();
        result.put("Semester", semester);
        return result;
    }

    Map<String, String> getDictionaryForCourse() {
        Map<String, String> result = new HashMap<>();
        result.put("Course", course);
        return result;
    }
}
