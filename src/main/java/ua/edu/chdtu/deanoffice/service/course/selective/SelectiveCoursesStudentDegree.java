package ua.edu.chdtu.deanoffice.service.course.selective;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectiveCoursesStudentDegree {
    private StudentDegree studentDegree;
    private List<SelectiveCourse> selectiveCourses;
}
