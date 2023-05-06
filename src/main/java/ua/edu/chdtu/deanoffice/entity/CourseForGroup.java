package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "courses_for_groups")
public class CourseForGroup extends BaseEntity {
    @ManyToOne
    private Course course;
    @ManyToOne
    private StudentGroup studentGroup;
    @ManyToOne
    private Teacher teacher;
    @Temporal(TemporalType.DATE)
    private Date examDate;
    private boolean academicDifference;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseForGroup)) return false;
        CourseForGroup that = (CourseForGroup) o;
        if (id != 0)
            return id == that.id;
        else
            return Objects.equals(course, that.course) &&
                Objects.equals(studentGroup, that.studentGroup);
    }

    @Override
    public int hashCode() {
        if (id != 0)
            return Objects.hash(id);
        else
            return Objects.hash(course, studentGroup);
    }
}
