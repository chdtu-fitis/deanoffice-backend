package ua.edu.chdtu.deanoffice.api.student.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.Sex;

import java.util.Date;

@Getter
@Setter
public class StudentDTO {
    @JsonView(StudentDegreeViews.SearchAndSimple.class)
    private Integer id;
    @JsonView(StudentDegreeViews.SearchAndSimple.class)
    private String name;
    @JsonView(StudentDegreeViews.SearchAndSimple.class)
    private String surname;
    @JsonView(StudentDegreeViews.SearchAndSimple.class)
    private String patronimic;
    @JsonView(StudentDegreeViews.DetailAndDegree.class)
    private String nameEng;
    @JsonView(StudentDegreeViews.DetailAndDegree.class)
    private String surnameEng;
    @JsonView(StudentDegreeViews.DetailAndDegree.class)
    private String patronimicEng;
    @JsonView(StudentDegreeViews.Simple.class)
    private String telephone;
    @JsonView(StudentDegreeViews.Detail.class)
    private Sex sex;
    @JsonView(StudentDegreeViews.SearchAndSimple.class)
    private Date birthDate;
    @JsonView(StudentDegreeViews.Detail.class)
    private String registrationAddress;
    @JsonView(StudentDegreeViews.Detail.class)
    private String studentCardNumber;
    @JsonView(StudentDegreeViews.Detail.class)
    private String actualAddress;
    @JsonView(StudentDegreeViews.Personal.class)
    private String school;
    @JsonView(StudentDegreeViews.Personal.class)
    private String fatherName;
    @JsonView(StudentDegreeViews.Personal.class)
    private String fatherPhone;
    @JsonView(StudentDegreeViews.Personal.class)
    private String fatherInfo;
    @JsonView(StudentDegreeViews.Personal.class)
    private String motherName;
    @JsonView(StudentDegreeViews.Personal.class)
    private String motherPhone;
    @JsonView(StudentDegreeViews.Personal.class)
    private String motherInfo;
    @JsonView(StudentDegreeViews.Personal.class)
    private String notes;
    @JsonView(StudentDegreeViews.Personal.class)
    private String email;
    @JsonView(StudentDegreeViews.Search.class)
    private String groups;
}
