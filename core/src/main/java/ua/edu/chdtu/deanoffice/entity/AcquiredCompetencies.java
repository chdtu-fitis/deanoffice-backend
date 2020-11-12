package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
    public class AcquiredCompetencies extends BaseEntity {
    private String competencies;
    @ManyToOne
    private Specialization specialization;
    private Integer year;
}
