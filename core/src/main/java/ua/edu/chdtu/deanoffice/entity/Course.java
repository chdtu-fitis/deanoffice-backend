package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Course extends BaseEntity {
    @ManyToOne
    @Cascade(CascadeType.ALL)
    private CourseName courseName;
    private Integer semester;
    @ManyToOne
    @JoinColumn(name = "kc_id")
    private KnowledgeControl knowledgeControl;
    private Integer hours;
    private Integer hoursPerCredit;
    private BigDecimal credits;
}
