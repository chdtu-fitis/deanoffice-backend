package ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.EducationDocument;
import ua.edu.chdtu.deanoffice.entity.Payment;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class StudentDegreeFullEdeboDataDto {
    private int id;
    private StudentDTO student;
    private SpecializationDTO specialization;
    private String previousDiplomaNumber;
    @JsonFormat(pattern="yyyy-MM-dd", locale = "uk_UA", timezone = "EET")
    private Date previousDiplomaDate;
    private EducationDocument previousDiplomaType;
    private String previousDiplomaIssuedBy;
    private String supplementNumber;
    @JsonFormat(pattern="yyyy-MM-dd", locale = "uk_UA", timezone = "EET")
    private Date admissionDate;
    private String admissionOrderNumber;
    @JsonFormat(pattern="yyyy-MM-dd", locale = "uk_UA", timezone = "EET")
    private Date admissionOrderDate;
    private Payment payment;
    private boolean modified;
    private Set<StudentPreviousUniversityDTO> studentPreviousUniversities;
}
