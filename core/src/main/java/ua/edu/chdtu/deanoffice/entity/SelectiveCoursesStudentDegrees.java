package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
public class SelectiveCoursesStudentDegrees extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    StudentDegree studentDegree;
    @ManyToOne(fetch = FetchType.LAZY)
    SelectiveCourse selectiveCourse;
}
