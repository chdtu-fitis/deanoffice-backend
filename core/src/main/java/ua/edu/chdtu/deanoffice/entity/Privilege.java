package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithActiveEntity;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class Privilege extends NameWithActiveEntity {

}
