package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.Person;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class Teacher extends Person {
    @ManyToOne
    private Department department;
    @ManyToOne
    private Position position;
    @Column(name = "scientific_degree")
    private String scientificDegree;
}
