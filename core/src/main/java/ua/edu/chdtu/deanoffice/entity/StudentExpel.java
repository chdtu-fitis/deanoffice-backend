package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Getter
@Setter
public class StudentExpel extends BaseEntity {
    @ManyToOne
    private StudentDegree studentDegree;
    @ManyToOne
    private StudentGroup studentGroup;
    private int studyYear;
    @Enumerated(value = EnumType.STRING)
    private Payment payment = Payment.BUDGET;
    @Temporal(TemporalType.DATE)
    private Date expelDate;
    private String orderNumber;
    @Temporal(TemporalType.DATE)
    private Date orderDate;
    @ManyToOne
    private OrderReason orderReason;
    @Temporal(TemporalType.DATE)
    private Date applicationDate;
}
