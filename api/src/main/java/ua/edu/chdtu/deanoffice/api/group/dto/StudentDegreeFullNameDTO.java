package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.PersonFullNameDTO;

@Getter
@Setter
class StudentDegreeFullNameDTO {
    @JsonView(StudentGroupView.WithStudents.class)
    private int id;
    @JsonView(StudentGroupView.WithStudents.class)
    private PersonFullNameDTO student;
}
