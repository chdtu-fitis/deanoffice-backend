package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngEntity;
import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class Degree extends NameWithEngEntity {
    @Column(name="qualification_level_descr")
    private String qualificationLevelDescription;
    @Column(name="qualification_level_descr_eng")
    private String qualificationLevelDescriptionEng;
    private String admissionRequirements;
    private String admissionRequirementsEng;
    private String admissionForeignRequirements;
    private String admissionForeignRequirementsEng;
    private String admissionShortenedRequirements;
    private String admissionShortenedRequirementsEng;
    private String furtherStudyAccess;
    private String furtherStudyAccessEng;
    private String professionalStatus;
    private String professionalStatusEng;

    public Degree() {
    }

    public Degree(int id, String name){
        setId(id);
        setName(name);
    }

    public Degree(String name, String nameEng) {
        super(name, nameEng);
    }
}
