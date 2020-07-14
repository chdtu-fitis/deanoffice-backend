package ua.edu.chdtu.deanoffice.entity.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter

@Entity
@NoArgsConstructor
@Accessors(chain = true)
@AllArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private OrderTemplateVersion orderTemplateVersion;
    @ManyToOne(fetch = FetchType.LAZY)
    private Faculty faculty;
    @Temporal(TemporalType.DATE)
    private Date orderDate;
    private String orderNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    private OrderApproveTemplate orderApproveTemplate;
    private String comment;
    private boolean active = true;
    private boolean signed = false;
}

