package ua.edu.chdtu.deanoffice.service.document.report.groupgrade;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Course;

@Getter
@Setter
public class CombinedCourse extends Course {

    private int numberOfSemesters;

    private int startingSemester;

    private boolean combined=false;


    CombinedCourse(Course course) {
        setCourseName(course.getCourseName());
        setSemester(course.getSemester());
        setKnowledgeControl(course.getKnowledgeControl());
        setHours(course.getHours());
        setHoursPerCredit(course.getHoursPerCredit());
        setCredits(course.getCredits());
    }

    CombinedCourse() {
    }

}
