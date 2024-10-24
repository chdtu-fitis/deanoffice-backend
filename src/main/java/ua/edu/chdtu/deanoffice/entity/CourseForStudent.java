package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "courses_for_students")
public class CourseForStudent extends BaseEntity {
    @ManyToOne
    private Course course;
    @ManyToOne
    private StudentDegree studentDegree;
    @ManyToOne
    private Teacher teacher;
    @Enumerated(value = EnumType.STRING)
    private CourseType courseType;
    public CourseForStudent() {}

    public CourseForStudent(Course course, StudentDegree studentDegree, Teacher teacher, CourseType courseType) {
        this.course = course;
        this.studentDegree = studentDegree;
        this.teacher = teacher;
        this.courseType = courseType;
    }
}
