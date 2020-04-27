package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.Person;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class NewTeacher extends Person {
    @ManyToOne
    private Department department;
    @ManyToOne
    private Position position;
    private boolean active = true;
    @ManyToOne
    private ScientificDegree scientificDegree;
}
