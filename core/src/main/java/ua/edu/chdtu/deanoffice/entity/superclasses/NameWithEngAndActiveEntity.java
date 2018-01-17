package ua.edu.chdtu.deanoffice.entity.superclasses;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class NameWithEngAndActiveEntity extends NameWithEngEntity {
    @Column(name="active", nullable = false)
    private boolean active = true;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
