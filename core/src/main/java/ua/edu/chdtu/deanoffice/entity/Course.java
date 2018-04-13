package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Column;
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
    @Column(name = "semester", nullable = false)
    private Integer semester;
    @ManyToOne
    @JoinColumn(name = "kc_id")
    private KnowledgeControl knowledgeControl;
    @Column(name = "hours", nullable = false)
    private Integer hours;
    @Column(name = "hours_per_credit", nullable = false)
    private Integer hoursPerCredit;
    @Column(name = "credits", nullable = false, precision = 4, scale = 1)
    private BigDecimal credits;
}
