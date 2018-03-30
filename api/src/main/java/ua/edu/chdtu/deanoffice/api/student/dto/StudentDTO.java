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
    @JsonView(StudentDegreeViews.SearchSimpleDegrees.class)
    private Integer id;
    @JsonView(StudentDegreeViews.SearchSimpleDegrees.class)
    private String name;
    @JsonView(StudentDegreeViews.SearchSimpleDegrees.class)
    private String surname;
    @JsonView(StudentDegreeViews.SearchSimpleDegrees.class)
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
    @JsonView(StudentDegreeViews.SearchSimpleDegrees.class)
    private Date birthDate;
    @JsonView(StudentDegreeViews.Detail.class)
    private String registrationAddress;
    @JsonView(StudentDegreeViews.DetailAndDegree.class)
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
    @JsonView(StudentDegreeViews.Degrees.class)
    private Set<StudentDegreeDTO> degrees;
}
