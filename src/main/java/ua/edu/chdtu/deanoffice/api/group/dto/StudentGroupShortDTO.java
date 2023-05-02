package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;

import java.util.List;

@Getter
@Setter
public class StudentGroupShortDTO {
    @JsonView(StudentGroupView.Basic.class)
    private Integer id;
    @JsonView(StudentGroupView.Basic.class)
    private String name;
    @JsonView(StudentGroupView.Basic.class)
    private int studySemesters;
    @JsonView(StudentGroupView.Basic.class)
    private TuitionForm tuitionForm;
    @JsonView(StudentGroupView.WithStudents.class)
    private List<StudentDegreeFullNameDTO> studentDegrees;
}
