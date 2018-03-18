package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "courses_for_groups", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"course_id", "studentgroup_id"})
})
public class CourseForGroup extends BaseEntity {
    @ManyToOne
    private Course course;
    @ManyToOne
    @JoinColumn(name = "studentgroup_id")
    private StudentGroup studentGroup;
    @ManyToOne
    private Teacher teacher;
    @Column(name = "exam_date")
    private Date examDate;
}
