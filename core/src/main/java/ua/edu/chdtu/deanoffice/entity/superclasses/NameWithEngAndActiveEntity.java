package ua.edu.chdtu.deanoffice.entity.superclasses;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public class NameWithEngAndActiveEntity extends NameWithEngEntity {
    @Column(name = "active", nullable = false)
    private boolean active = true;
}
