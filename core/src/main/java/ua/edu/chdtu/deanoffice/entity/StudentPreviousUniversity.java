package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Getter
@Setter
public class StudentPreviousUniversity extends BaseEntity {
    @ManyToOne
    private StudentDegree studentDegree;
    private String universityName;
    @Temporal(TemporalType.DATE)
    private Date studyStartDate;
    @Temporal(TemporalType.DATE)
    private Date studyEndDate;
    private String academicCertificateNumber;
    @Temporal(TemporalType.DATE)
    private Date academicCertificateDate;
}
