package ua.edu.chdtu.deanoffice.api.studentDegree.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.Sex;

import java.util.Date;

@Getter
@Setter
public class StudentDTO {
    private StudentGroupDTO studentGroup;
    private Integer id;
    private String name;
    private String nameEng;
    private String surname;
    private String surnameEng;
    private String patronimic;
    private String patronimicEng;
    private Sex sex;
    private Date birthDate;
    private String registrationAddress;
    private String actualAddress;
    private String recordBookNumber;
    private String telephone;
}
