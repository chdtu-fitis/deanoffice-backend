package ua.edu.chdtu.deanoffice.entity.order;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;


@Getter
@Setter
@Entity
public class OrderApproveTemplate extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private OrderApprover mainApprover;
    @ManyToOne
    private OrderApprover initiatorApprover;
    @ManyToOne(fetch = FetchType.LAZY)
    private Faculty faculty;
    private boolean active;
}
