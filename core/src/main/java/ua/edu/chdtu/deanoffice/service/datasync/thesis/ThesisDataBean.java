package ua.edu.chdtu.deanoffice.service.datasync.thesis;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

@Getter
@Setter
public class ThesisDataBean {
    private Integer studentDegreeId;
    private String fullName;
    private String thesisName;
    private String thesisNameEng;
    private String oldThesisName;

    public ThesisDataBean(StudentDegree studentDegree, String thesisName, String thesisNameEng){
        Student s = studentDegree.getStudent();
        this.studentDegreeId = studentDegree.getId();
        this.fullName = s.getName()+" "+s.getSurname()+" "+s.getPatronimic();
        oldThesisName = studentDegree.getThesisName();
    }
}
