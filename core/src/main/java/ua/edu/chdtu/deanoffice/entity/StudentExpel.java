package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "student_expel")
public class StudentExpel extends BaseEntity {
    @ManyToOne
    private Student student;
    @Column(name = "expel_date", nullable = false)
    private Date expelDate;
    @Column(name = "order_number", nullable = false, length = 15)
    private String orderNumber;
    @Column(name = "order_date", nullable = false)
    private Date orderDate;
    @ManyToOne
    private StudentGroup group;
    @ManyToOne
    private OrderReason reason;
    @Column(name = "application_date", nullable = false)
    private Date applicationDate;
}
