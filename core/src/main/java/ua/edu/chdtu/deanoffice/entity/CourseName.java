package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "course_name")
public class CourseName extends NameWithEngEntity {
    @Column(name = "abbreviation", nullable = true, length = 15)
    private String abbreviation;
}
