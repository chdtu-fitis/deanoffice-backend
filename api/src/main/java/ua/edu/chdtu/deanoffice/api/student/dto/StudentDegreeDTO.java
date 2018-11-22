package ua.edu.chdtu.deanoffice.api.student.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.specialization.dto.SpecializationDTO;
import ua.edu.chdtu.deanoffice.entity.EducationDocument;
import ua.edu.chdtu.deanoffice.entity.Payment;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class StudentDegreeDTO {
    @JsonView(StudentView.SimpleAndDegrees.class)
    private Integer id;
    @JsonView(StudentView.Simple.class)
    private StudentDTO student;
    @JsonView(StudentView.SimpleAndDegrees.class)
    private NamedDTO studentGroup;
    @JsonView(StudentView.SimpleAndDegrees.class)
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
    @JsonView(StudentView.WithSpecilization.class)
    private SpecializationDTO specialization;
    @JsonView(StudentView.WithActive.class)
    private boolean active;
    @JsonView(StudentView.DetailAndDegree.class)
    private String studentCardNumber;
    @JsonView(StudentView.Degrees.class)
    private Date admissionOrderDate;
    @JsonView(StudentView.Degrees.class)
    private String admissionOrderNumber;
    @JsonView(StudentView.Degrees.class)
    private Date contractDate;
    @JsonView(StudentView.Degrees.class)
    private String contractNumber;
    @JsonView(StudentView.Degrees.class)
    private String previousDiplomaIssuedBy;
    @JsonView(StudentView.Degrees.class)
    private String previousDiplomaIssuedByEng;
    @JsonView(StudentView.Degrees.class)
    private Date admissionDate;

    @JsonView(StudentView.Degrees.class)
    private Integer studentGroupId;
    @JsonView(StudentView.Degrees.class)
    private Integer specializationId;

    @JsonView(StudentView.Degrees.class)
    private boolean diplomaWithHonours;
    @JsonView(StudentView.Degrees.class)
    private Set<StudentPreviousUniversityDTO> studentPreviousUniversities = new HashSet<>();
}
