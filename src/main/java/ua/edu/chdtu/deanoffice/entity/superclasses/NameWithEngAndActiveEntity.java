package ua.edu.chdtu.deanoffice.entity.superclasses;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public class NameWithEngAndActiveEntity extends NameWithEngEntity {
    private boolean active = true;
}
