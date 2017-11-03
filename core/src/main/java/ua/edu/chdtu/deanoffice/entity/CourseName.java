package ua.edu.chdtu.deanoffice.entity;

import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class CourseName extends NameWithEngEntity {
    @Column(name="abbreviation", nullable = true, length = 15)
    private String abbreviation;

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

}
