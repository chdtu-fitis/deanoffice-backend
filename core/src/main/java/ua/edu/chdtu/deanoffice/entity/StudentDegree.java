package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class StudentDegree extends BaseEntity {
    private boolean active;
    @Temporal(TemporalType.DATE)
    private Date admissionOrderDate;
    private String admissionOrderNumber;
    @Temporal(TemporalType.DATE)
    private Date admissionDate;
    @Temporal(TemporalType.DATE)
    private Date contractDate;
    private String contractNumber;
    private String diplomaNumber;
    @Temporal(TemporalType.DATE)
    private Date diplomaDate;
    private boolean diplomaWithHonours;
    @Enumerated(value = EnumType.STRING)
    private Payment payment = Payment.BUDGET;
    @Temporal(TemporalType.DATE)
    private Date previousDiplomaDate;
    @Enumerated(value = EnumType.STRING)
    private EducationDocument previousDiplomaType = EducationDocument.SECONDARY_SCHOOL_CERTIFICATE;
    private String previousDiplomaNumber;
    private String previousDiplomaIssuedBy;
    private String previousDiplomaIssuedByEng;
    @Temporal(TemporalType.DATE)
    private Date protocolDate;
    private String protocolNumber;
    private String recordBookNumber;
    private String studentCardNumber;
    @Temporal(TemporalType.DATE)
    private Date supplementDate;
    private String supplementNumber;
    private String thesisName;
    private String thesisNameEng;
    private String thesisSupervisor;
    @ManyToOne(fetch = FetchType.LAZY)
    private Specialization specialization;
    @ManyToOne
    private Student student;
    @ManyToOne
    private StudentGroup studentGroup;
    @OneToMany(mappedBy = "studentDegree", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<StudentPreviousUniversity> studentPreviousUniversities = new HashSet<>();
}
