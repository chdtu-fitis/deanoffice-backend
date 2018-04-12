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
@Table(name = "renewed_expelled_student")
public class RenewedExpelledStudent extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "student_expel_id", nullable = false)
    private StudentExpel studentExpel;

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

    @Column(name = "academic_certificate_number", nullable = false)
    private String academicCertificateNumber;

    @Column(name = "academic_certificate_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date academicCertificateDate;

    @Column(name = "academic_certificate_issued_by", nullable = false)
    private Date academicCertificateIssuedBy;
}
