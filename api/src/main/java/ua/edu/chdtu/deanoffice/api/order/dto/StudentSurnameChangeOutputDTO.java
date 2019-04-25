package ua.edu.chdtu.deanoffice.api.order.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.FacultyDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDegreeDTO;

import java.util.Date;

@Getter
@Setter
public class StudentSurnameChangeOutputDTO {
    private int id;
    private Date orderDate;
    private String orderNumber;
    private FacultyDTO faculty;
    private StudentDegreeDTO studentDegreeId;
    private Date surnameChangeDate;
    private String facultyName;
    private String specialityName;
    private String specializationName;
    private Integer studentYear;
    private String studentGroupName;
    private String tuitionForm;
    private String payment;
    private Date applicationDate;
    private String applicationBasedOn;
    private String oldSurname;
    private String newSurname;
}
