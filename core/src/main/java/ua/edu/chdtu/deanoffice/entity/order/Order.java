package ua.edu.chdtu.deanoffice.entity.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.entity.OrderReason;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter

@Entity
@NoArgsConstructor
@AllArgsConstructor

@Accessors(chain = true)
@Table(name = "orders")
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private OrderTemplateVersion orderTemplateVersion;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Faculty faculty;

    private Date orderDate;

    private String orderNumber;

    @ManyToOne
    private OrderApproveTemplate orderApproveTemplate;

    private String comment;

    private String orderParagraph;

    private Boolean active =false;

    private Boolean signed = false;
}

