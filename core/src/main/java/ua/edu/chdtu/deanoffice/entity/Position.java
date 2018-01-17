package ua.edu.chdtu.deanoffice.entity;

import ua.edu.chdtu.deanoffice.entity.superclasses.NameEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
//TODO cr: краще явно вказувати таблицю - @Table
public class Position extends NameEntity {
}
