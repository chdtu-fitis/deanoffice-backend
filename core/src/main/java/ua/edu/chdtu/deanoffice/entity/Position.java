package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameEntity;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class Position extends NameEntity {
}
