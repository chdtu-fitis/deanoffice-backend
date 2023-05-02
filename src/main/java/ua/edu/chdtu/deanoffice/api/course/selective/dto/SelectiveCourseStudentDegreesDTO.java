package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class SelectiveCourseStudentDegreesDTO {
    private SelectiveCourseDTO selectiveCourse;
    private List<StudentDegreeDTO> studentDegrees;
}
