package ua.edu.chdtu.deanoffice.entity.superclasses;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class NameWithEngEntity extends NameEntity {
    @Column(name="name_eng", nullable = false, length = 100)
    private String nameEng;

    public NameWithEngEntity() {
    }

    public NameWithEngEntity(String name, String nameEng) {
        this.setName(name);
        this.nameEng = nameEng;
    }

    public String getNameEng() {
        return nameEng;
    }

    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
    }
}
