package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Getter
@Setter
public class Course extends BaseEntity {
    @ManyToOne
    private CourseName courseName;
    private Integer semester;
    @ManyToOne
    @JoinColumn(name = "kc_id")
    private KnowledgeControl knowledgeControl;
    private Integer hours;
    private Integer hoursPerCredit;
    private BigDecimal credits;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return getId() == course.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCourseName(), getSemester(), getKnowledgeControl(), getHours(), getHoursPerCredit());
    }
}
