package ua.edu.chdtu.deanoffice.entity.superclasses;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
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
    char sex = 'm';
    //TODO cr: use enum instead
}
