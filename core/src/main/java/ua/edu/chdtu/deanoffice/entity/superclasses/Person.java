package ua.edu.chdtu.deanoffice.entity.superclasses;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Person extends BaseEntity {
    @Column(name = "surname", nullable = false, length = 20)
    private String surname;
    @Column(name = "name", nullable = false, length = 20)
    private String name;
    @Column(name = "patronimic", nullable = false, length = 20)
    private String patronimic;
    @Column(name = "active", nullable = false)
    private boolean active = true;
    @Column(name = "sex", nullable = false)
    private char sex = 'm';
    //TODO cr: use enum instead

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronimic() {
        return patronimic;
    }

    public void setPatronimic(String patronimic) {
        this.patronimic = patronimic;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }
}
