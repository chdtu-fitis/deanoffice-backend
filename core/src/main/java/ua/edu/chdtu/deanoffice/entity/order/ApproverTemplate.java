package ua.edu.chdtu.deanoffice.entity.order;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter

@Entity
@Table(name = "order_approver_template")
public class ApproverTemplate extends BaseEntity {

    @ManyToOne
    private OrderApprover orderApprover;
//    @Column(name = "order_approver_id")
//    @Type(type = "packageofclass.IntArrayUserType")
//    private Integer[] approverIds;
    @ManyToOne(fetch = FetchType.LAZY)
    private Faculty faculty;
    private boolean active;
}
