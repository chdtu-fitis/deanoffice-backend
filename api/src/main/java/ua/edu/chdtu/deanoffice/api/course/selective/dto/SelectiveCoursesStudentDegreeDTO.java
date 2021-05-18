package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SelectiveCoursesStudentDegreeDTO {
    private StudentDegreeDTO studentDegree;
    private List<SelectiveCourseDTO> selectiveCourses;
}
