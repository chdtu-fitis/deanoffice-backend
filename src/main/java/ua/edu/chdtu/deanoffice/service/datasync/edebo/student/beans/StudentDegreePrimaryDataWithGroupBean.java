package ua.edu.chdtu.deanoffice.service.datasync.edebo.student.beans;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

@Getter
@Setter
public class StudentDegreePrimaryDataWithGroupBean extends StudentDegreePrimaryDataBean {
    private String groupName;
    private String edeboId;
    public StudentDegreePrimaryDataWithGroupBean(StudentDegree studentDegree){
        super(studentDegree);
        if (studentDegree.getStudentGroup() != null)
            this.groupName = studentDegree.getStudentGroup().getName();
        this.edeboId = studentDegree.getEdeboId();
    }
}
