package ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.EducationDocument;
import ua.edu.chdtu.deanoffice.entity.Payment;

import java.util.Date;

@Getter
@Setter
public class StudentDegreeFullEdeboDataDto {
    private int id;
    private StudentDTO student;
    private SpecializationDTO specialization;
    private String previousDiplomaNumber;
    private Date previousDiplomaDate;
    private EducationDocument previousDiplomaType;
    private String previousDiplomaIssuedBy;
    private String supplementNumber;
    private Date admissionDate;
    private String admissionOrderNumber;
    private Date admissionOrderDate;
    private Payment payment;

}
