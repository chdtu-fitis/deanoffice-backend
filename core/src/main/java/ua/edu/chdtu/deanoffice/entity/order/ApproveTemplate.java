package ua.edu.chdtu.deanoffice.entity.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Faculty;

import javax.persistence.*;

@Getter
@Setter

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_approve_template")
public class ApproveTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private SingleApprover executionControl;
    private SingleApprover headApprover;
    private SingleApprover introducedBy;
    private SingleApprover approver;
    private Faculty faculty;
    private boolean active;

}
