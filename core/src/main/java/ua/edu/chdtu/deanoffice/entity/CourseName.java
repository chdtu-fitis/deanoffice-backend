package ua.edu.chdtu.deanoffice.entity;

import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="course_name")
public class CourseName extends NameWithEngEntity {
    @Column(name="abbreviation", nullable = true, length = 15)
    private String abbreviation;
//TODO cr: старайтеся писати в одному стилі. Якщо використовуйте ломбок в ентітях то використовуйте завжди
    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

}
