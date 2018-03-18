package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngAndActiveEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class Faculty extends NameWithEngAndActiveEntity {
    //TODO Це не є помилкою, але а на замітку: якщо не потрібно вказати іншу назву колонки, то JPA(Hibernate?) сам візьме з назви
    // поля та перетворить в snake_case
    @Column(name = "abbr", nullable = false, unique = true, length = 20)
    private String abbr;
    @Column(name = "dean", length = 70)
    private String dean;
}
