package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "student_expel")
public class StudentExpel extends BaseEntity {
    @ManyToOne
    private StudentDegree studentDegree;
    @Column(name = "expel_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date expelDate;
    @Column(name = "order_number", nullable = false, length = 15)
    private String orderNumber;
    @Column(name = "order_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date orderDate;
    @ManyToOne
    private OrderReason reason;
    @Column(name = "application_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date applicationDate;
}
