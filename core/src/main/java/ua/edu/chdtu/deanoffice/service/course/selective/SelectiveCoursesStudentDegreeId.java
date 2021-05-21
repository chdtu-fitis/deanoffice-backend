package ua.edu.chdtu.deanoffice.service.course.selective;

import lombok.Data;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;

import java.util.List;

@Data
public class SelectiveCoursesStudentDegreeId {
    private ExistingId studentDegree;
    private List<SelectiveCourse> selectiveCourses;
}
