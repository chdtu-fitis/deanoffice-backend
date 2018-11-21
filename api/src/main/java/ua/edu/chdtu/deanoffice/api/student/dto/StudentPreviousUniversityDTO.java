package ua.edu.chdtu.deanoffice.api.student.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class StudentPreviousUniversityDTO {
    @JsonView(StudentView.Degrees.class)
    private Integer id;
    private StudentDegreeDTO studentDegree;
    @JsonView(StudentView.Degrees.class)
    private String universityName;
    @JsonView(StudentView.Degrees.class)
    private Date studyStartDate;
    @JsonView(StudentView.Degrees.class)
    private Date studyEndDate;
    @JsonView(StudentView.Degrees.class)
    private String academicCertificateNumber;
    @JsonView(StudentView.Degrees.class)
    private Date academicCertificateDate;
}
