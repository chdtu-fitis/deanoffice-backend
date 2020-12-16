package ua.edu.chdtu.deanoffice.service.document.report.exam.beans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseExamReportDataBean {
    private String yearShort = "";
    private String examDate = "";
    private String courseName = "";
    private String semester = "";
    private String hours = "";
    private String knowledgeControlName = "";
    private String teacherName = "";
    private String teacherInitials = "";
}
