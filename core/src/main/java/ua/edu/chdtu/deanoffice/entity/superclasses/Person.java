package ua.edu.chdtu.deanoffice.entity.superclasses;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public class Person extends BaseEntity {
    private String surname;
    private String name;
    private String patronimic;
    @Enumerated(value = EnumType.STRING)
    private Sex sex = Sex.MALE;

    public String getFullNameUkr() {
        return getSurname() + " " + getName() + " " + getPatronimic();
    }

    public String getInitialsUkr() {
        return getSurname() + " " + getName().substring(0, 1) + ". " + getPatronimic().substring(0, 1) + ".";
    }
}
