package ua.edu.chdtu.deanoffice.service.document.report.exam.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentExamReportDataBean {
    private String surname = "";
    private String name = "";
    private String patronimic = "";
    private String recordBookNumber = "";
}
