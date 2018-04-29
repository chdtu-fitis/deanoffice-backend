package ua.edu.chdtu.deanoffice.api.student.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.entity.EducationDocument;
import ua.edu.chdtu.deanoffice.entity.Payment;

import java.util.Date;

@Getter
@Setter
public class StudentDegreeDTO {
    private Integer studentGroupId;
    @JsonView(StudentView.SimpleAndDegrees.class)
    private NamedDTO studentGroup;
    @JsonView(StudentView.SimpleAndDegrees.class)
    private Integer id;
    @JsonView(StudentView.Simple.class)
    private StudentDTO student;
    @JsonView(StudentView.DetailAndDegree.class)
    private String recordBookNumber;
    @JsonView(StudentView.DetailAndDegree.class)
    private String diplomaNumber;
    @JsonView(StudentView.DetailAndDegree.class)
    private Date diplomaDate;
    @JsonView(StudentView.DetailAndDegree.class)
    private String supplementNumber;
    @JsonView(StudentView.DetailAndDegree.class)
    private Date supplementDate;
    @JsonView(StudentView.DetailAndDegree.class)
    private String thesisName;
    @JsonView(StudentView.DetailAndDegree.class)
    private String thesisNameEng;
    @JsonView(StudentView.DetailAndDegree.class)
    private String protocolNumber;
    @JsonView(StudentView.DetailAndDegree.class)
    private Date protocolDate;
    @JsonView(StudentView.DetailAndDegree.class)
    private EducationDocument previousDiplomaType;
    @JsonView(StudentView.DetailAndDegree.class)
    private String previousDiplomaNumber;
    @JsonView(StudentView.DetailAndDegree.class)
    private Date previousDiplomaDate;
    @JsonView(StudentView.SimpleAndDegrees.class)
    private Payment payment;
    @JsonView(StudentView.Degree.class)
    private NamedDTO degree;
    @JsonView(StudentView.WithActive.class)
    private boolean active;
    @JsonView(StudentView.DetailAndDegree.class)
    private String studentCardNumber;
}
