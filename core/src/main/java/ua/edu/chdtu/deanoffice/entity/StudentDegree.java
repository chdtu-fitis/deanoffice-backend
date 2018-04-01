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
@Table(name = "student_degree")
public class StudentDegree extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "studentgroup_id")
    private StudentGroup studentGroup;
    @ManyToOne
    private Degree degree;
    @Column(name = "record_book_number", length = 15)
    private String recordBookNumber;
    @Column(name = "admission_order_number", length = 15)
    private String admissionOrderNumber;
    @Column(name = "admission_order_date")
    @Temporal(TemporalType.DATE)
    private Date admissionOrderDate;
    @Column(name = "contract_number", length = 15)
    private String contractNumber;
    @Column(name = "contract_date")
    @Temporal(TemporalType.DATE)
    private Date contractDate;
    @Column(name = "diploma_number", length = 15)
    private String diplomaNumber;
    @Column(name = "diploma_date")
    @Temporal(TemporalType.DATE)
    private Date diplomaDate;
    @Column(name = "supplement_number", length = 15)
    private String supplementNumber;
    @Column(name = "supplement_date")
    @Temporal(TemporalType.DATE)
    private Date supplementDate;
    @Column(name = "thesis_name", length = 150)
    private String thesisName;
    @Column(name = "thesis_name_eng", length = 150)
    private String thesisNameEng;
    @Column(name = "protocol_number", length = 10)
    private String protocolNumber;
    @Column(name = "protocol_date")
    @Temporal(TemporalType.DATE)
    private Date protocolDate;
    @Column(name = "previous_diploma_type", nullable = false, length = 30, columnDefinition = "varchar(30) default 'SECONDARY_SCHOOL_CERTIFICATE'")
    @Enumerated(value = EnumType.STRING)
    private EducationDocument previousDiplomaType = EducationDocument.SECONDARY_SCHOOL_CERTIFICATE;
    @Column(name = "previous_diploma_number", length = 15)
    private String previousDiplomaNumber;
    @Column(name = "previous_diploma_date")
    @Temporal(TemporalType.DATE)
    private Date previousDiplomaDate;
    @Column(name = "payment", nullable = false, length = 8, columnDefinition = "varchar(8) default 'BUDGET'")
    @Enumerated(value = EnumType.STRING)
    private Payment payment = Payment.BUDGET;
    @Column(name = "active", nullable = false)
    private boolean active;
}
