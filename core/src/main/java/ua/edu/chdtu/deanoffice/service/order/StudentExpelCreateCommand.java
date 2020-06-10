package ua.edu.chdtu.deanoffice.service.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Payment;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentExpelCreateCommand {
    private Date applicationDate;
    private Date expelDate;
    private Payment payment;
    private int studyYear;
    private Integer orderReason;
    private Integer studentDegreeId;
    private Integer studentGroupId;
    private OrderParsedParagraphDto orderParsedParagraphDto;
    private Integer orderId;
    private Integer orderApproverTemplateId;
    private Integer orderControlTemplateId;
    private String orderComment;
}
