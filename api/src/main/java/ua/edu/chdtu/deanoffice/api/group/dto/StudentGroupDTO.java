package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentGroupDTO {
    @JsonView(StudentGroupView.Basic.class)
    private Integer id;
    @JsonView(StudentGroupView.Basic.class)
    private String name;
    @JsonView(StudentGroupView.GroupData.class)
    private int studySemesters;
    @JsonView(StudentGroupView.Basic.class)
    private int creationYear;
    @JsonView(StudentGroupView.WithStudents.class)
    private List<StudentDegreeFullNameDTO> studentDegrees;
}