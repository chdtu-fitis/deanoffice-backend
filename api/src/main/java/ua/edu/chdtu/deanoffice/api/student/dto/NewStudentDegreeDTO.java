package ua.edu.chdtu.deanoffice.api.student.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewStudentDegreeDTO extends BaseStudentDegree {
    private Integer studentGroupId;
    private Integer degreeId;
}
