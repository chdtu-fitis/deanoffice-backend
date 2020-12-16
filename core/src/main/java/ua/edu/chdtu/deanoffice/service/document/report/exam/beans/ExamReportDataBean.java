package ua.edu.chdtu.deanoffice.service.document.report.exam.beans;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ExamReportDataBean {
    private CourseExamReportDataBean courseExamReportDataBean;
    private GroupExamReportDataBean groupExamReportDataBean;
    private List<StudentExamReportDataBean> studentExamReportDataBeans;
}
