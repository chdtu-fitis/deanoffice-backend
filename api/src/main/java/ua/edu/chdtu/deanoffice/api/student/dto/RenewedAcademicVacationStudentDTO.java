package ua.edu.chdtu.deanoffice.api.student.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.entity.Payment;

import java.util.Date;

@Getter
@Setter
public class RenewedAcademicVacationStudentDTO {
    private Integer id;
    private StudentAcademicVacationDTO studentAcademicVacation;
    private int studyYear;
    private Payment payment;
    private NamedDTO studentGroup;
    private Date renewDate;
    private Date applicationDate;
    private Date expelDate;
    private String orderNumber;
    private Date orderDate;

    private Integer studentGroupId;
    private Integer studentAcademicVacationId;
}
