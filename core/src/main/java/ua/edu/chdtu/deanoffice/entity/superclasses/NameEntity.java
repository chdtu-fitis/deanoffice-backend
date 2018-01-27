package ua.edu.chdtu.deanoffice.entity.superclasses;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
@Getter
@Setter
public class NameEntity extends BaseEntity {
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    public String toString() {
        return this.name;
    }
}
