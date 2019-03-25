package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;
import java.util.Date;

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
}
