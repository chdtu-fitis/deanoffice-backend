package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithActiveEntity;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class Department extends NameWithActiveEntity {
    private String abbr;
    @ManyToOne(fetch = FetchType.LAZY)
    private Faculty faculty;
    private String webSite;
}
