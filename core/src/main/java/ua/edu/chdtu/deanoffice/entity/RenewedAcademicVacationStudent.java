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

@Getter
@Setter
@Entity
@Table(name = "renewed_academic_vacation_student")
public class RenewedAcademicVacationStudent extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "student_academic_vacation_id", nullable = false)
    private StudentAcademicVacation studentAcademicVacation;

    @Column(name = "study_year", nullable = false)
    private int studyYear;

    @Column(name = "payment", nullable = false, length = 8, columnDefinition = "varchar(8) default 'BUDGET'")
    @Enumerated(value = EnumType.STRING)
    private Payment payment = Payment.BUDGET;

    @ManyToOne
    @JoinColumn(name = "student_group_id", nullable = false)
    private StudentGroup studentGroup;

    @Column(name = "renew_date")
    @Temporal(TemporalType.DATE)
    private Date renewDate;

    @Column(name = "application_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date applicationDate;
}
