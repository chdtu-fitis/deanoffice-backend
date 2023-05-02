package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class SelectiveCoursesStudentDegreeWriteManyDTO {
    @NotNull
    @NotEmpty
    private List<Integer> selectiveCourses;
    @NotNull
    @NotEmpty
    private List<Integer> studentDegrees;
    @NotNull
    private int studyYear;
}
