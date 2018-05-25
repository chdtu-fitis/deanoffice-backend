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
import javax.persistence.UniqueConstraint;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "qualifications_for_specializations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"professional_qualification_id", "specialization_id", "year"})
})
public class QualificationForSpecialization extends BaseEntity {

    @ManyToOne
    @JoinColumn(nullable = false)
    private Specialization specialization;
    @ManyToOne
    @JoinColumn(nullable = false, name = "professional_qualification_id")
    private ProfessionalQualification professionalQualification;
    @Column(nullable = false, name = "year")
    private Integer year;
}
