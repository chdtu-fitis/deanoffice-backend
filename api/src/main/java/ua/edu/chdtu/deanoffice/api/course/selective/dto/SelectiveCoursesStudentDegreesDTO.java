package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.validation.ExistingIdDTO;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import java.util.List;

@Getter
@Setter
public class SelectiveCoursesStudentDegreesDTO {
    private int id;
    private List<Integer> selectiveCourse;
    private ExistingIdDTO studentDegree;
}
