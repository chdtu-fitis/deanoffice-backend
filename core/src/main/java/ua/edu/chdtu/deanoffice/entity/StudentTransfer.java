package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;
//import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@Getter
@Setter
public class StudentTransfer extends BaseEntity {
    @Temporal(TemporalType.DATE)
    private Date orderDate;
    private String orderNumber;
    private Integer oldStudyYear;
    private Integer newStudyYear;
    private Integer oldSpecializationId;
    private Integer newSpecializationId;
    @Enumerated(value = EnumType.STRING)
    private Payment oldPayment = Payment.BUDGET;
    @Enumerated(value = EnumType.STRING)
    private Payment newPayment = Payment.BUDGET;
    @Temporal(TemporalType.DATE)
    private Date applicationDate;
    private String contractNumber;
    @Temporal(TemporalType.DATE)
    private Date contractDate;
    private Integer studentDegreeId;
    private Integer oldStudentGroupId;
    private Integer newStudentGroupId;
//    @ManyToOne
//    private StudentDegree studentDegree;
//    @ManyToOne
//    private StudentGroup studentGroup;
}
