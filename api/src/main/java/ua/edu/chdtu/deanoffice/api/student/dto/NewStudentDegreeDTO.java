package ua.edu.chdtu.deanoffice.api.student.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.EducationDocument;
import ua.edu.chdtu.deanoffice.entity.Payment;

import java.util.Date;

@Getter
@Setter
public class NewStudentDegreeDTO {
    private Integer studentId;
    private Integer studentGroupId;
    private Integer degreeId;
    private String recordBookNumber;
    private String diplomaNumber;
    private Date diplomaDate;
    private String supplementNumber;
    private Date supplementDate;
    private String thesisName;
    private String thesisNameEng;
    private String protocolNumber;
    private Date protocolDate;
    private EducationDocument previousDiplomaType;
    private String previousDiplomaNumber;
    private Date previousDiplomaDate;
    private Payment payment;
}
