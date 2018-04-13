package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
    @Column(name = "exam_date")
    private Date examDate;
}
