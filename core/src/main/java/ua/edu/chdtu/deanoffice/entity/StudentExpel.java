package ua.edu.chdtu.deanoffice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentExpel extends BaseEntity {
    @ManyToOne
    private StudentDegree studentDegree; // ступінь
    @ManyToOne
    private StudentGroup studentGroup;  // група
    private int studyYear;
    @Enumerated(value = EnumType.STRING)
    private Payment payment = Payment.BUDGET; // бюджет чи контракт
    @Temporal(TemporalType.DATE)
    private Date expelDate; // дата вибування
    private String orderNumber; // номер наказу про вибування
    @Temporal(TemporalType.DATE)
    private Date orderDate; // дата наказу
    @ManyToOne
    private OrderReason orderReason; // причина наказу
    @Temporal(TemporalType.DATE)
    private Date applicationDate; // дата
}
