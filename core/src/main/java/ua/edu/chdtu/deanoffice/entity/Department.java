package ua.edu.chdtu.deanoffice.entity;

import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithActiveEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Department extends NameWithActiveEntity {
    @Column(name="abbr", nullable = false, length = 20)
    private String abbr;
    @ManyToOne
    private Faculty faculty;

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

}
