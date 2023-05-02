package ua.edu.chdtu.deanoffice.api.student;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.*;

import java.util.Date;

@Getter
@Setter
public class ExpelledOrRenewedStudentBean {
    private String operation;
    private Integer expelOrRenewId;
    private int expelOrRenewStudyYear;
    private Payment expelOrRenewPayment;
    private Date expelOrRenewDate;
    private String expelOrRenewOrderNumber;
    private Date expelOrRenewOrderDate;
    private OrderReason orderReason;
    private Date expelOrRenewApplicationDate;

    public ExpelledOrRenewedStudentBean(StudentExpel studentExpel) {
        this.expelOrRenewId = studentExpel.getId();
        this.expelOrRenewStudyYear = studentExpel.getStudyYear();
        this.expelOrRenewPayment = studentExpel.getPayment();
        this.expelOrRenewDate = studentExpel.getExpelDate();
        this.expelOrRenewOrderNumber = studentExpel.getOrderNumber();
        this.expelOrRenewOrderDate = studentExpel.getOrderDate();
        this.orderReason = studentExpel.getOrderReason();
        this.expelOrRenewApplicationDate = studentExpel.getApplicationDate();
        if (orderReason != null){
            this.operation = "Відраховано: ";
        }
    }

    public ExpelledOrRenewedStudentBean(RenewedExpelledStudent renewedExpelledStudent) {
        this.expelOrRenewId = renewedExpelledStudent.getId();
        this.expelOrRenewStudyYear = renewedExpelledStudent.getStudyYear();
        this.expelOrRenewDate = renewedExpelledStudent.getRenewDate();
        this.expelOrRenewApplicationDate = renewedExpelledStudent.getApplicationDate();
        this.expelOrRenewOrderDate = renewedExpelledStudent.getOrderDate();
        this.expelOrRenewOrderNumber = renewedExpelledStudent.getOrderNumber();
        if (orderReason == null){
            this.operation = "Поновлено: ";
        }
    }

    public ExpelledOrRenewedStudentBean() {
    }
}
