package ua.edu.chdtu.deanoffice.api.student.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class StudentPreviousUniversityDTO {
    private Integer id;
    private StudentDegreeDTO studentDegree;
    private String universityName;
    private Date studyStartDate;
    private Date studyEndDate;
    private String academicCertificateNumber;
    private Date academicCertificateDate;
}
