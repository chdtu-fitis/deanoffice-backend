package ua.edu.chdtu.deanoffice.entity.superclasses;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class NameWithEngEntity extends NameEntity {
    //TODO cr: можливо не потрібна така глибока вложенісь. На цьому наслідуванні ви майже нічого не виграєте а код запутаєте
    @Column(name="name_eng", length = 100)
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
