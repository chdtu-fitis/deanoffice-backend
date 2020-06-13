package ua.edu.chdtu.deanoffice.service.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.service.order.dto.OrderParsedParagraphDto;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentExpelCreateCommand {
    private Integer studentDegreeId;
    private Integer orderApproverTemplateId;
    private Integer orderControlTemplateId;
    private OrderParsedParagraphDto orderParsedParagraphDto;
    private Date expelDate;
    private String orderComment;
}
