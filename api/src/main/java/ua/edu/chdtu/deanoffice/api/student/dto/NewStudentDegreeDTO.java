package ua.edu.chdtu.deanoffice.api.student.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewStudentDegreeDTO extends BaseStudentDegreeDTO {
    private Integer studentGroupId;
}
