package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Data;

import java.util.List;

@Data
public class SelectiveCoursesStudentDegreeDTO {
    private StudentDegreeDTO studentDegree;
    private List<SelectiveCourseDTO> selectiveCourses;
}
