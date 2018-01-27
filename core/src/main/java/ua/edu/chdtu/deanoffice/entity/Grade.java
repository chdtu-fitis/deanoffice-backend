package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "grade", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"course_id", "student_id"})
})
public class Grade extends BaseEntity {
    @ManyToOne
    private Course course;
    @ManyToOne
    private Student student;
    @Column(name = "grade", nullable = false)
    private int grade;
    @Column(name = "points", nullable = false)
    private int points;
    @Column(name = "ects", nullable = false, length = 2)
    private String ects;
}
