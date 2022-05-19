package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class GroupStudentsRegistrationResult {
    List<CoursesSelectedByStudentsGroup> coursesSelectedByStudentsGroup;
    List<StudentNameAndId> groupStudentsWithNoSelectedCourses;

    public GroupStudentsRegistrationResult(List<CoursesSelectedByStudentsGroup> coursesSelectedByStudentsGroup, List<StudentNameAndId> groupStudentsWithNoSelectedCourses) {
        this.coursesSelectedByStudentsGroup = coursesSelectedByStudentsGroup;
        this.groupStudentsWithNoSelectedCourses = groupStudentsWithNoSelectedCourses;
    }

    public GroupStudentsRegistrationResult() {
    }
}
