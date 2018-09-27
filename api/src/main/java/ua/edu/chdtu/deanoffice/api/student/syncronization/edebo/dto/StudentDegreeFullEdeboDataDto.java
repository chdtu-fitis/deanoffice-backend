package ua.edu.chdtu.deanoffice.api.student.syncronization.edebo.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.*;

import java.util.Date;

@Getter
@Setter
public class StudentDegreeFullEdeboDataDto {
    private int id;
    private Student student;
    private Specialization specializationName;
    private String diplomaNumber;
    private Date previousDiplomaDate;
    private EducationDocument previousDiplomaType;
    private String supplementNumber;
    private Date admissionDate;
    private String admissionOrderNumber;
    private Date admissionOrderDate;
    private String surnameEng;
    private String nameEng;
    private String patronimicEng;
}
