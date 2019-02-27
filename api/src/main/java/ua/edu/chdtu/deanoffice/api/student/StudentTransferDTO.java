package ua.edu.chdtu.deanoffice.api.student;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Payment;

import java.util.Date;

@Getter
@Setter
public class StudentTransferDTO {
    private Integer transferId;
    private Date orderDate;
    private String orderNumber;
    private Integer oldStudyYear;
    private Integer newStudyYear;
    private Integer oldSpecializationId;
    private Integer newSpecializationId;
    private Payment oldPayment;
    private Payment newPayment;
    private Date applicationDate;
    private String contractNumber;
    private Date contractDate;
    private Integer studentDegreeId;
    private Integer oldStudentGroupId;
    private Integer newStudentGroupId;
}
