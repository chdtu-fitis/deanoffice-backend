package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.validation.ExistingIdDTO;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class SelectiveCoursesStudentDegreeWriteDTO {
    @NotNull
    private List<Integer> selectiveCourses;
    @NotNull
    private ExistingIdDTO studentDegree;
}
