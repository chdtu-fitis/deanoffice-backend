package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class ExtraPoints extends BaseEntity {
    @ManyToOne
    private StudentDegree studentDegree;
    private int semester;
    private int points;
}
