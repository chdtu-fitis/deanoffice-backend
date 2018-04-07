package ua.edu.chdtu.deanoffice.api.student.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;

import java.util.Date;

@Getter
@Setter
public class StudentExpelDTO {
    @JsonView(StudentView.Expel.class)
    private Integer id;
    @JsonView(StudentView.Expel.class)
    private StudentDegreeDTO studentDegree;
    @JsonView(StudentView.Expel.class)
    private Date expelDate;
    @JsonView(StudentView.Expel.class)
    private String orderNumber;
    @JsonView(StudentView.Expel.class)
    private Date orderDate;
    @JsonView(StudentView.Expel.class)
    private NamedDTO reason;
    @JsonView(StudentView.Expel.class)
    private Date applicationDate;

    private Integer[] studentDegreeIds;
    private Integer reasonId;
}
