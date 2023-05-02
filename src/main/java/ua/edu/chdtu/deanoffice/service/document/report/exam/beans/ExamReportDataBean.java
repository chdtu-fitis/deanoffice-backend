package ua.edu.chdtu.deanoffice.service.document.report.exam.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamReportDataBean {
    private CourseExamReportDataBean courseExamReportDataBean;
    private GroupExamReportDataBean groupExamReportDataBean;
    private List<StudentExamReportDataBean> studentExamReportDataBeans;
}
