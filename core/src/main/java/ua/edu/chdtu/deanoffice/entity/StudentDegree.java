package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Getter
@Setter
@Entity
public class StudentDegree extends BaseEntity {
    @ManyToOne
    private Student student;
    @ManyToOne
    private StudentGroup studentGroup;
    @ManyToOne(fetch = FetchType.LAZY)
    private Specialization specialization;
    private String recordBookNumber;
    private String studentCardNumber;
    private String admissionOrderNumber;
    @Temporal(TemporalType.DATE)
    private Date admissionOrderDate;
    @Temporal(TemporalType.DATE)
    private Date admissionDate;
    private String contractNumber;
    @Temporal(TemporalType.DATE)
    private Date contractDate;
    private String diplomaNumber;
    @Temporal(TemporalType.DATE)
    private Date diplomaDate;
    private String supplementNumber;
    @Temporal(TemporalType.DATE)
    private Date supplementDate;
    private String thesisName;
    private String thesisNameEng;
    private String protocolNumber;
    @Temporal(TemporalType.DATE)
    private Date protocolDate;
    @Enumerated(value = EnumType.STRING)
    private EducationDocument previousDiplomaType = EducationDocument.SECONDARY_SCHOOL_CERTIFICATE;
    private String previousDiplomaNumber;
    private String previousDiplomaIssuedBy;
    @Temporal(TemporalType.DATE)
    private Date previousDiplomaDate;
    @Enumerated(value = EnumType.STRING)
    private Payment payment = Payment.BUDGET;
    private boolean active;
}
