package ua.edu.chdtu.deanoffice.api.student.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Payment;

import java.util.Date;

@Getter
@Setter
public class StudentTransferDTO {
    private Integer id;
    private Date orderDate;
    private String orderNumber;
    private Integer newStudyYear;
    private Integer newSpecializationId;
    private Payment newPayment;
    private Date applicationDate;
    private String contractNumber;
    private Date contractDate;
    private Integer studentDegreeId;
    private Integer newStudentGroupId;
}
