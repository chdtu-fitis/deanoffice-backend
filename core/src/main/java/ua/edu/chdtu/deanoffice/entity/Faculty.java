package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngAndActiveEntity;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Faculty extends NameWithEngAndActiveEntity {
    private String abbr;
    private String dean;
    private String deanEng;
    private String genitiveCase;

    public Faculty(int id) {
        setId(id);
    }
}
