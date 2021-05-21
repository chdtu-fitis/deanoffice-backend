package ua.edu.chdtu.deanoffice.service.course.selective;

import lombok.Data;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import java.util.List;

@Data
public class SelectiveCoursesStudentDegree {
    private StudentDegree studentDegree;
    private List<SelectiveCourse> selectiveCourses;
}
