package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Data;
import ua.edu.chdtu.deanoffice.api.general.dto.validation.ExistingIdDTO;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SelectiveCoursesStudentDegreeSubstitutionDTO {
    @NotNull
    @NotEmpty
    private List<Integer> selectiveCoursesIdsToAdd;
    @NotNull
    @NotEmpty
    private List<Integer> selectiveCoursesIdsToDrop;
    @NotNull
    private ExistingIdDTO studentDegree;
    @NotNull
    private int studyYear;
}
