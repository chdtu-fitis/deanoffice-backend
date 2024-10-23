package ua.edu.chdtu.deanoffice.entity.superclasses;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.Teacher;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseCourse extends BaseEntity {

    @ManyToOne
    protected Course course;

    @ManyToOne
    protected Teacher teacher;
}
