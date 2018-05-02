package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class CurrentYear extends BaseEntity {
    private int currYear;
}
