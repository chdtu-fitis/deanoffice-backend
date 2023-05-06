package ua.edu.chdtu.deanoffice.service.course;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;

import java.util.List;

@Getter
@Setter
public class ForeignStudentsSynchronizationBean {
    private List<Course> common;
    private List<Course> differentForeignCourses;
    private List<Course> differentOtherCourses;
    private StudentGroup otherGroup;
    private StudentGroup foreignGroup;
}
