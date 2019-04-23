package ua.edu.chdtu.deanoffice.api.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class StudentSurnameChangeInputDTO {

    private Date orderDate;
    private String orderNumber;
    private Integer facultyId;
    private Integer studentDegreeId;
    private Date surnameChangeDate;
    private Date applicationDate;
    private String applicationBasedOn;
    private String newSurname;
}
