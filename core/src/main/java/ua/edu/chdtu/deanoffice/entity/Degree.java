package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name="degree", uniqueConstraints = {@UniqueConstraint(columnNames = {"id", "name"})})
public class Degree extends NameWithEngEntity {
    @Column(name = "qualification_level_descr")
    private String qualificationLevelDescription;
    @Column(name = "qualification_level_descr_eng")
    private String qualificationLevelDescriptionEng;
    @Column(name = "admission_requirements")
    private String admissionRequirements;
    @Column(name = "admission_requirements_eng")
    private String admissionRequirementsEng;
    @Column(name = "further_study_access")
    private String furtherStudyAccess;
    @Column(name = "further_study_access_eng")
    private String furtherStudyAccessEng;
    @Column(name = "professional_status")
    private String professionalStatus;
    @Column(name = "professional_status_eng")
    private String professionalStatusEng;

    public Degree() {
    }

    public Degree(String name, String nameEng) {
        super(name, nameEng);
    }
}
