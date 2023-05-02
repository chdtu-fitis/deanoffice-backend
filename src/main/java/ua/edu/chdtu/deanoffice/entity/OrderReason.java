package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngAndActiveEntity;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class OrderReason extends NameWithEngAndActiveEntity {
    private String kind;
}
