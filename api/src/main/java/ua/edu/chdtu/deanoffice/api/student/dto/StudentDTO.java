package ua.edu.chdtu.deanoffice.api.student.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.Sex;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class StudentDTO {
    @JsonView(StudentView.SearchSimpleDegrees.class)
    private Integer id;
    @JsonView(StudentView.SearchSimpleDegrees.class)
    private String name;
    @JsonView(StudentView.SearchSimpleDegrees.class)
    private String surname;
    @JsonView(StudentView.SearchSimpleDegrees.class)
    private String patronimic;
    @JsonView(StudentView.DetailAndDegree.class)
    private String nameEng;
    @JsonView(StudentView.DetailAndDegree.class)
    private String surnameEng;
    @JsonView(StudentView.DetailAndDegree.class)
    private String patronimicEng;
    @JsonView(StudentView.Simple.class)
    private String telephone;
    @JsonView(StudentView.Detail.class)
    private Sex sex;
    @JsonView(StudentView.SearchSimpleDegrees.class)
    private Date birthDate;
    @JsonView(StudentView.Detail.class)
    private String registrationAddress;
    @JsonView(StudentView.Detail.class)
    private String actualAddress;
    @JsonView(StudentView.Personal.class)
    private String school;
    @JsonView(StudentView.Personal.class)
    private String fatherName;
    @JsonView(StudentView.Personal.class)
    private String fatherPhone;
    @JsonView(StudentView.Personal.class)
    private String fatherInfo;
    @JsonView(StudentView.Personal.class)
    private String motherName;
    @JsonView(StudentView.Personal.class)
    private String motherPhone;
    @JsonView(StudentView.Personal.class)
    private String motherInfo;
    @JsonView(StudentView.Personal.class)
    private String notes;
    @JsonView(StudentView.Personal.class)
    private String email;
    @JsonView(StudentView.Search.class)
    private String groups;
    @JsonView(StudentView.Personal.class)
    private String photoUrl;
    @JsonView(StudentView.Degrees.class)
    private Set<StudentDegreeDTO> degrees;
}
