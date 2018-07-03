package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.util.Prototype;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class QualificationForSpecialization extends BaseEntity implements Prototype<QualificationForSpecialization> {
    @ManyToOne
    private Specialization specialization;
    @ManyToOne
    private ProfessionalQualification professionalQualification;
    private Integer year;

    @Override
    public QualificationForSpecialization clone() {
        QualificationForSpecialization qualificationForSpecialization = new QualificationForSpecialization();
        qualificationForSpecialization.setSpecialization(specialization);
        qualificationForSpecialization.setYear(year);
        qualificationForSpecialization.setProfessionalQualification(professionalQualification);
        qualificationForSpecialization.setId(getId());
        return qualificationForSpecialization;
    }
}
