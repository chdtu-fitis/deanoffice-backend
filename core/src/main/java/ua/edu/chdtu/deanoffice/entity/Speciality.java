package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngAndActiveEntity;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class Speciality extends NameWithEngAndActiveEntity {
    private String code;
    private String fieldOfStudyCode;
    private String fieldOfStudy;
    private String fieldOfStudyEng;
    private String nameGenitive;
    @ManyToOne
    private FieldOfKnowledge fieldOfKnowledge;
}
