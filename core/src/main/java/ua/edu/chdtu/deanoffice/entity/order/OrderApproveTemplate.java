package ua.edu.chdtu.deanoffice.entity.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.List;

@Getter
@Setter

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class OrderApproveTemplate extends BaseEntity {

    @ManyToOne
    private OrderApprover mainApprover;

    @ElementCollection
    private List<Long> approverIds;

    @ManyToOne
    private OrderApprover initiatorApprover;

    @ManyToOne
    private Faculty faculty;

    private Boolean active;
}
