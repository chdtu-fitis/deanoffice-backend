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
    private String qualificationLevelDescription;
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
    private String nationalQualificationFrameworkLevel;
    private String nationalQualificationFrameworkLevelEng;

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
