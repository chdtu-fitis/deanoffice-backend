package ua.edu.chdtu.deanoffice.entity;

import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="degree", uniqueConstraints = {@UniqueConstraint(columnNames = {"id", "name"})})
public class Degree extends NameWithEngEntity {

    public Degree() {
    }

    public Degree(String name, String nameEng) {
        super(name, nameEng);
    }
}
