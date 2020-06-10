package ua.edu.chdtu.deanoffice.entity.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter

@Entity
@NoArgsConstructor
@AllArgsConstructor

public class OrderControlTemplate extends BaseEntity {

    private String controlText;

    @ManyToOne
    private Faculty faculty;

    private Boolean active;
}

