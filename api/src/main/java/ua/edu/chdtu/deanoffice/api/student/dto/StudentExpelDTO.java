package ua.edu.chdtu.deanoffice.api.student.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.OrderReasonDTO;

import java.util.Date;

@Getter
@Setter
public class StudentExpelDTO {
    @JsonView(StudentView.Expel.class)
    private Integer id;
    @JsonView(StudentView.Simple.class)
    private StudentDegreeDTO studentDegree;
    @JsonView(StudentView.Simple.class)
    private Date expelDate;
    @JsonView(StudentView.Simple.class)
    private String orderNumber;
    @JsonView(StudentView.Simple.class)
    private Date orderDate;
    @JsonView(StudentView.Simple.class)
    private OrderReasonDTO reason;
    @JsonView(StudentView.Simple.class)
    private Date applicationDate;

    private Integer[] studentDegreeIds;
    private Integer reasonId;
}
