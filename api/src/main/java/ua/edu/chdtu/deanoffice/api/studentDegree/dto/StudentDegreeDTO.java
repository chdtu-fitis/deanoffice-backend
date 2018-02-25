package ua.edu.chdtu.deanoffice.api.studentDegree.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class StudentDegreeDTO {
    private Integer id;
    private DegreeDTO degree;
    private StudentDTO student;
    private String diplomaNumber;
    private Date diplomaDate;
    private String supplementNumber;
    private Date supplementDate;
    private String thesisName;
    private String protocolNumber;
    private Date protocolDate;
    private String previousDiplomaNumber;
    private Date previousDiplomaDate;
    private boolean awarded;
}
