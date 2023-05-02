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
public class AcquiredCompetencies extends BaseEntity {
    private String competencies;
    private String competenciesEng;
    @ManyToOne
    private Specialization specialization;
    private Integer year;
}
