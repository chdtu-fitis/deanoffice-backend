package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import java.util.Date;


@Getter
@Setter
@Entity
@Table(name = "acquired_competencies", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"specialization_id", "year"})
})
public class AcquiredCompetencies extends BaseEntity {

    @Column(name = "competencies", nullable = false)
    private String competencies;
    @Column(name = "competencies_eng", nullable = false)
    private String competenciesEng;
    @JoinColumn(nullable = false)
    @ManyToOne
    private Specialization specialization;
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date year;
}
