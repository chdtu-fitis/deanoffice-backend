package ua.edu.chdtu.deanoffice.service.document.report.groupgrade;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Course;

@Getter
@Setter
public class CombinedCourse extends Course {

    private int numberOfSemesters;

    private int startingSemester;


    CombinedCourse(Course course) {
        setCourseName(course.getCourseName());
        setSemester(course.getSemester());
        setKnowledgeControl(course.getKnowledgeControl());
        setHours(course.getHours());
        setHoursPerCredit(course.getHours());
        setCredits(course.getCredits());
    }

    CombinedCourse() {
    }

}
