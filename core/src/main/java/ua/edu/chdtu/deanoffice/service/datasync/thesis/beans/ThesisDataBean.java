package ua.edu.chdtu.deanoffice.service.datasync.thesis.beans;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.datasync.thesis.ThesisImportData;

@Getter
@Setter
public class ThesisDataBean {
    private Integer studentDegreeId;
    private String fullName;
    private String thesisName;
    private String thesisNameEng;
    private String oldThesisName;
    private String fullSupervisorName;

    public ThesisDataBean(StudentDegree studentDegree,
                          String thesisName,
                          String thesisNameEng,
                          String fullSupervisorName) {
        Student s = studentDegree.getStudent();
        this.studentDegreeId = studentDegree.getId();
        this.fullName = s.getSurname() + " " + s.getName() + " " + s.getPatronimic();
        this.oldThesisName = studentDegree.getThesisName();
        this.thesisName = thesisName;
        this.thesisNameEng = thesisNameEng;
        this.fullSupervisorName = fullSupervisorName;
    }

    public ThesisDataBean(ThesisImportData thesisImportData) {
        this.fullName = thesisImportData.getStudentFullName();
        this.thesisName = thesisImportData.getThesisName();
        this.thesisNameEng = thesisImportData.getThesisNameEng();
        this.fullSupervisorName = thesisImportData.getFullSupervisorName();
    }
}
