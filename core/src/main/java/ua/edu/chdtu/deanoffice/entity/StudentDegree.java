package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "student_degree")
public class StudentDegree extends BaseEntity {
    @ManyToOne
    private Student student;
    @ManyToOne
    private Degree degree;
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
    @Column(name = "previous_diploma_number", length = 15)
    private String previousDiplomaNumber;
    @Column(name = "previous_diploma_date")
    @Temporal(TemporalType.DATE)
    private Date previousDiplomaDate;
    @Column(name = "awarded", nullable = false)
    private boolean awarded;
}
