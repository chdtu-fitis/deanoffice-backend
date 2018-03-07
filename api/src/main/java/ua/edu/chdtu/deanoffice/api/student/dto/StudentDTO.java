package ua.edu.chdtu.deanoffice.api.student.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.Sex;

import java.util.Date;

@Getter
@Setter
public class StudentDTO {
    @JsonView(StudentDegreeViews.Search.class)
    private Integer id;
    @JsonView(StudentDegreeViews.Search.class)
    private String name;
    @JsonView(StudentDegreeViews.Search.class)
    private String surname;
    @JsonView(StudentDegreeViews.Search.class)
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
    @JsonView(StudentDegreeViews.Search.class)
    private Date birthDate;
    @JsonView(StudentDegreeViews.Detail.class)
    private String registrationAddress;
    @JsonView(StudentDegreeViews.Detail.class)
    private String studentCardNumber;
    @JsonView(StudentDegreeViews.Detail.class)
    private String actualAddress;
    private String school;
    private String fatherName;
    private String fatherPhone;
    private String fatherInfo;
    private String motherName;
    private String motherPhone;
    private String motherInfo;
    private String notes;
    private String email;
}
