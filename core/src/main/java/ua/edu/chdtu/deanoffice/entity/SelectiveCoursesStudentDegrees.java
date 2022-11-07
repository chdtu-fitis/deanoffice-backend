package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class SelectiveCoursesStudentDegrees extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private StudentDegree studentDegree;
    @ManyToOne(fetch = FetchType.LAZY)
    private SelectiveCourse selectiveCourse;
    private boolean active;
    @Temporal(TemporalType.DATE)
    private Date createdAt;

    public SelectiveCoursesStudentDegrees() {
    }

    public SelectiveCoursesStudentDegrees(StudentDegree studentDegree, SelectiveCourse selectiveCourse) {
        this.studentDegree = studentDegree;
        this.selectiveCourse = selectiveCourse;
        this.active = true;
    }
}
