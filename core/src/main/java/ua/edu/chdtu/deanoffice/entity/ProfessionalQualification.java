package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@Entity(name = "professional_qualification")
public class ProfessionalQualification extends NameWithEngEntity {
    @Column(name = "code", nullable = false)
    private String code;
}
