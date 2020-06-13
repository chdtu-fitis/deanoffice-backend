package ua.edu.chdtu.deanoffice.service.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class StudentExpelBusinessInformation {
    private Integer studentDegreeId;
    private Integer orderId;
}
