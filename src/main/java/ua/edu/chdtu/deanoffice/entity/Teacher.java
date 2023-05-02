package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.Person;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class Teacher extends Person {
    @ManyToOne(fetch=FetchType.LAZY)
    private Department department;
    @ManyToOne(fetch=FetchType.LAZY)
    private Position position;
    private boolean active = true;
    @ManyToOne(fetch=FetchType.LAZY)
    private ScientificDegree scientificDegree;
    @Column(name="academic_title")
    @Enumerated(value = EnumType.STRING)
    private AcademicTitle academicTitle;
}
