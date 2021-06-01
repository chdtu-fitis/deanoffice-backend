package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import ua.edu.chdtu.deanoffice.api.general.dto.validation.ExistingIdDTO;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class SelectiveCoursesStudentDegreeWriteDTO {
    @NotNull
    @NotEmpty
    private List<Integer> selectiveCourses;
    @NotNull
    private ExistingIdDTO studentDegree;
}
