package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

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

//    @OneToMany(mappedBy = "studentGroup", fetch = FetchType.LAZY)
//    private List<CourseForGroup> coursesForGroup;
}
