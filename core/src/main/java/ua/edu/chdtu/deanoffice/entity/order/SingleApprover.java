package ua.edu.chdtu.deanoffice.entity.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SingleApprover {
    private Integer id;
    private String position;
    private String initials;
}
