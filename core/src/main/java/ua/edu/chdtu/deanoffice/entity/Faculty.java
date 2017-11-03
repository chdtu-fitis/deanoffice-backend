package ua.edu.chdtu.deanoffice.entity;

import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngAndActiveEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Faculty extends NameWithEngAndActiveEntity {
    @Column(name="abbr", nullable = false, unique = true, length = 20)
    private String abbr;
    //DEAN

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }
}
