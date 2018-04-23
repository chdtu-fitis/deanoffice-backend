package ua.edu.chdtu.deanoffice.entity.superclasses;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public class NameEntity extends BaseEntity {
    private String name;
    public String toString() {
        return this.name;
    }
}
