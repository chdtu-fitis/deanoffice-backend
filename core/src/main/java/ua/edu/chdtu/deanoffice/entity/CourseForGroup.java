package ua.edu.chdtu.deanoffice.entity;

import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="courses_for_groups")
public class CourseForGroup extends BaseEntity {
    @ManyToOne
    private Course course;
    @ManyToOne
    @JoinColumn(name="studentgroup_id")
    private StudentGroup studentGroup;
    @ManyToOne
    private Teacher teacher;
    @Column(name="exam_date", nullable = true)
    private Date examDate;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public StudentGroup getStudentGroup() {
        return studentGroup;
    }

    public void setStudentGroup(StudentGroup studentGroup) {
        this.studentGroup = studentGroup;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }
}
