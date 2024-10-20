package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseCourse;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "courses_for_students")
public class CourseForStudent extends BaseCourse {
    @ManyToOne
    private StudentDegree studentDegree;
    @Enumerated(value = EnumType.STRING)
    private CourseType courseType;
    public CourseForStudent() {}

    public CourseForStudent(Course course, StudentDegree studentDegree, Teacher teacher, CourseType courseType) {
        this.course = course;
        this.studentDegree = studentDegree;
        this.teacher = teacher;
        this.courseType = courseType;
    }

    public boolean equalsByCourseAndTeacher(CourseForStudent obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        return this.course.equals(obj.course) && this.teacher.equals(obj.teacher);
    }
}
