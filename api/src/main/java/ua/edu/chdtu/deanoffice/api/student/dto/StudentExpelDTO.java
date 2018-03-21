package ua.edu.chdtu.deanoffice.api.student.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.OrderReasonDTO;

import java.util.Date;

@Getter
@Setter
public class StudentExpelDTO {
    private Integer studentDegreeId;
    private Integer reasonId;
    @JsonView(StudentDegreeViews.Expel.class)
    private Integer id;
    @JsonView(StudentDegreeViews.Simple.class)
    private StudentDegreeDTO studentDegree;
    @JsonView(StudentDegreeViews.Simple.class)
    private Date expelDate;
    @JsonView(StudentDegreeViews.Simple.class)
    private String orderNumber;
    @JsonView(StudentDegreeViews.Simple.class)
    private Date orderDate;
    @JsonView(StudentDegreeViews.Simple.class)
    private OrderReasonDTO reason;
    @JsonView(StudentDegreeViews.Simple.class)
    private Date applicationDate;
}
