package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngAndActiveEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class Faculty extends NameWithEngAndActiveEntity {
    @Column(name = "abbr", nullable = false, unique = true, length = 20)
    private String abbr;
    @Column(name = "dean", length = 70)
    private String dean;
}
