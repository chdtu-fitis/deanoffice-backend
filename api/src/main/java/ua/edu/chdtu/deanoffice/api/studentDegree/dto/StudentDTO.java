package ua.edu.chdtu.deanoffice.api.studentDegree.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.Sex;

import java.util.Date;

@Getter
@Setter
class StudentDTO {
    private StudentGroupDTO studentGroup;
    private Integer id;
    private String surname;
    private String name;
    private String patronimic;
    private boolean active;
    private Sex sex;
    private Date birthDate;
    private String registrationAddress;
    private String actualAddress;
    private String recordBookNumber;
    private String telephone;
}
