package ua.edu.chdtu.deanoffice.api.order.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentExpelParagraphCommand implements ParagraphCreateCommand {

    private String studentFullName;
    private String studyYear;
    private String studentDegree;
    private String tuitionForm;
    private String groupName;
    private String specialization;
    private String speciality;
    private String dataVstupu;
    private String orderReason;
    private String orderCause;
}
