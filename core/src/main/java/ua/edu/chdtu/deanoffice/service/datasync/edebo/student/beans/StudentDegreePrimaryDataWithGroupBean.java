package ua.edu.chdtu.deanoffice.service.datasync.edebo.student.beans;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

@Getter
@Setter
public class StudentDegreePrimaryDataWithGroupBean extends StudentDegreePrimaryDataBean {
    private String groupName;
    public StudentDegreePrimaryDataWithGroupBean(StudentDegree studentDegree){
        super(studentDegree);
        this.groupName = studentDegree.getStudentGroup().getName();
    }
}
