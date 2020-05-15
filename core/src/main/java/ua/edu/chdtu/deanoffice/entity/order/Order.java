package ua.edu.chdtu.deanoffice.entity.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.entity.OrderReason;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private Version version;
//
//    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private Faculty faculty;
//
//    private LocalDate date;
//
//    private String name;
//
//    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private OrderReason reason;
//
//    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private ApproveTemplate approveTemplate;
//
//    @OneToOne
//    private UserInputSet userInputSet;
//
//    private String comment;
//
//    private Boolean active;
//
//    private Boolean signed;
}
