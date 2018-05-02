package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "current_year")
public class CurrentYear extends BaseEntity {
    //TODO cr: тут або таблиця невдало названа, або тут і справді буде тільки 1 рядок, але тоді не зрозуміло для чого чому це не можна зробити без БД
    @Column(name = "curr_year")
    private int currYear;
}
