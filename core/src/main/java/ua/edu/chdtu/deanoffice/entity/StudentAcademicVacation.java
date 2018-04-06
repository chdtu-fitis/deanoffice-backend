package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "student_academic_vacation")
public class StudentAcademicVacation extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "studentdegree_id")
    private StudentDegree studentDegree;
    @Column(name = "vacation_start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date vacationStartDate;
    @Column(name = "vacation_end_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date vacationEndDate;
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
