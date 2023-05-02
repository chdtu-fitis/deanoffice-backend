package ua.edu.chdtu.deanoffice.api.studyyear.finish;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class StudyYearFinishDTO {
    List<Integer> ids;
    Date expelDate;
    Date orderDate;
    String orderNumber;
}
