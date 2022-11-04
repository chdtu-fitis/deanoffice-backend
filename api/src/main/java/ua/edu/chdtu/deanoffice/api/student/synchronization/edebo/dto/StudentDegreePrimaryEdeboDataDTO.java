package ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class StudentDegreePrimaryEdeboDataDTO {
    private String lastName;
    private String firstName;
    private String middleName;
    private String facultyName;
    private String birthday;
    private String degreeName;
    private String fullSpecialityName ;
    private String fullSpecializationName;
    private String groupName;
    private String edeboId;
}
