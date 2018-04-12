package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "student_academic_vacation")
public class StudentAcademicVacation extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "student_degree_id")
    private StudentDegree studentDegree;

    @ManyToOne
    @JoinColumn(name = "student_group_id", nullable = false)
    private StudentGroup studentGroup;

    @Column(name = "study_year", nullable = false)
    private int studyYear;

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
    private OrderReason orderReason;

    @Column(name = "application_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date applicationDate;

    @Column(name = "extra_information")
    private String extraInformation;
}
