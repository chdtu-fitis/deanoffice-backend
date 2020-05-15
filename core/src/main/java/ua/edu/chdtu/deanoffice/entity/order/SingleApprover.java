package ua.edu.chdtu.deanoffice.entity.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Entity;

@Getter
@Setter

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SingleApprover extends BaseEntity {
    private String position;
    private String initials;
}
