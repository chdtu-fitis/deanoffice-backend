package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngEntity;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class ProfessionalQualification extends NameWithEngEntity {
    private String code;
}
