package ua.edu.chdtu.deanoffice.entity.superclasses;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class NameEntity extends BaseEntity {
    @Column(name="name", nullable = false, length = 100)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return this.name;
    }
}
