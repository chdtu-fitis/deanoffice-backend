package ua.edu.chdtu.deanoffice.service.document.report.exam.beans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupExamReportDataBean {
    private String groupName = "";
    private String speciality = "";
    private String specializationName = "";
    private String facultyAbbr = "";
    private String deanInitials = "";
    private String degreeName = "";
    private String academicYear = "";
    private String groupStudyYear = "";
}
