package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithActiveEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class Department extends NameWithActiveEntity {
    @Column(name = "abbr", nullable = false, length = 20)
    private String abbr;
    @ManyToOne
    private Faculty faculty;
}
