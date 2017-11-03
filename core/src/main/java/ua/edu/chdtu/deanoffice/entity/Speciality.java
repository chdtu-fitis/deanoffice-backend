package ua.edu.chdtu.deanoffice.entity;

import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngAndActiveEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Speciality extends NameWithEngAndActiveEntity {
    @Column(name="code", nullable = false, unique = true, length = 20)
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
