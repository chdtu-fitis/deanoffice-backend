package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngEntity;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class FieldOfKnowledge extends NameWithEngEntity {
    private String code;
    private String codeInternational;
    private String nameInternational;
}
