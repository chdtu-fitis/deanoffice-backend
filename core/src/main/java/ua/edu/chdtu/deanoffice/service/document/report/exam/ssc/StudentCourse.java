package ua.edu.chdtu.deanoffice.service.document.report.exam.ssc;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

@Getter
@Setter
public class StudentCourse {
    private StudentDegree studentDegree;
    private Course course;
    private CourseForGroup courseForGroup;

    public StudentCourse(StudentDegree studentDegree, Course course, CourseForGroup courseForGroup) {
        this.studentDegree = studentDegree;
        this.course = course;
        this.courseForGroup = courseForGroup;
    }
}
