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
    private StudentExpel studentExpel;
    private int studyYear;
    @Enumerated(value = EnumType.STRING)
    private Payment payment = Payment.BUDGET;
    @ManyToOne
    private StudentGroup studentGroup;
    @Temporal(TemporalType.DATE)
    private Date renewDate;
    @Temporal(TemporalType.DATE)
    private Date applicationDate;
    private String academicCertificateNumber;
    @Temporal(TemporalType.DATE)
    private Date academicCertificateDate;
    private String academicCertificateIssuedBy;
}
