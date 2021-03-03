package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithActiveEntity;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngAndActiveEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class Department extends NameWithEngAndActiveEntity {
    private String abbr;
    @ManyToOne(fetch = FetchType.LAZY)
    private Faculty faculty;
    private String webSite;
}
