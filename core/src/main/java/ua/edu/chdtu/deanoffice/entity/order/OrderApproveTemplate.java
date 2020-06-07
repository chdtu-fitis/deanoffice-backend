package ua.edu.chdtu.deanoffice.entity.order;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;


@Getter
@Setter
@Entity
public class OrderApproveTemplate extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private OrderApprover mainApprover;
    @ManyToMany
    @JoinTable(
            name = "order_approve_template_approvers",
            joinColumns = @JoinColumn(name = "order_approve_template_id"),
            inverseJoinColumns = @JoinColumn(name = "approver_id"))
    private List<OrderApprover> approvers;
    @ManyToOne
    private OrderApprover initiatorApprover;
    @ManyToOne(fetch = FetchType.LAZY)
    private Faculty faculty;
    private boolean active;
}
