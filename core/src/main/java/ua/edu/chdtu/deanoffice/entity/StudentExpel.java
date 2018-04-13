package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "student_expel")
public class StudentExpel extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "student_degree_id")
    private StudentDegree studentDegree;

    @ManyToOne
    @JoinColumn(name = "student_group_id", nullable = false)
    private StudentGroup studentGroup;

    @Column(name = "study_year", nullable = false)
    private int studyYear;

    @Column(name = "payment", nullable = false, length = 8, columnDefinition = "varchar(8) default 'BUDGET'")
    @Enumerated(value = EnumType.STRING)
    private Payment payment = Payment.BUDGET;

    @Column(name = "expel_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date expelDate;

    @Column(name = "order_number", nullable = false, length = 15)
    private String orderNumber;

    @Column(name = "order_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date orderDate;

    @ManyToOne
    private OrderReason orderReason;

    @Column(name = "application_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date applicationDate;
}
