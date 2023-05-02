package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.validation.ExistingIdDTO;
import java.util.List;

@Getter
@Setter
public class SelectiveCoursesStudentDegreeIdDTO {
    private ExistingIdDTO studentDegree;
    private List<SelectiveCourseDTO> selectiveCourses;
}
