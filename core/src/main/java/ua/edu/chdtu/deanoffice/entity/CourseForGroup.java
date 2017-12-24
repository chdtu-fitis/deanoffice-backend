package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
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
    @Column(name = "is_graded")
    private Boolean graded;
}
