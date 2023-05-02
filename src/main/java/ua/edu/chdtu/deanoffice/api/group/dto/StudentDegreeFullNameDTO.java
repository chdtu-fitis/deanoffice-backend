package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.PersonFullNameDTO;

import java.util.Date;

@Getter
@Setter
public class StudentDegreeFullNameDTO {
    @JsonView(StudentGroupView.WithStudents.class)
    private int id;
    @JsonView(StudentGroupView.WithStudents.class)
    private PersonFullNameDTO student;
    @JsonView(StudentGroupView.WithStudents.class)
    private String recordBookNumber;
    @JsonView(StudentGroupView.WithExtendedStudentData.class)
    private String diplomaNumber;
    @JsonView(StudentGroupView.WithExtendedStudentData.class)
    private Date diplomaDate;
}
