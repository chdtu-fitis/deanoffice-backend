package ua.edu.chdtu.deanoffice.entity;

import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name="grade", uniqueConstraints={
        @UniqueConstraint(columnNames = {"course_id", "student_id"})
})
public class Grade extends BaseEntity {
    @ManyToOne
    private Course course;
    @ManyToOne
    private Student student;
    @Column(name="grade", nullable = false)
    private int grade;
    @Column(name="points", nullable = false)
    private int points;
    @Column(name="ects", nullable = false, length = 2)
    private String ects;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getEcts() {
        return ects;
    }

    public void setEcts(String ects) {
        this.ects = ects;
    }
}
