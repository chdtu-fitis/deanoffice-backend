package ua.edu.chdtu.deanoffice.service.datasync.edebo.student.beans;

import lombok.Getter;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

@Getter
public class UnmatchedSecondaryDataStudentDegreeBlueBean {
    private StudentDegree studentDegreeFromData, studentDegreeFromDb;
    public UnmatchedSecondaryDataStudentDegreeBlueBean(StudentDegree studentDegreeFromData, StudentDegree studentDegreeFromDb) {
        this.studentDegreeFromData = studentDegreeFromData;
        this.studentDegreeFromDb = studentDegreeFromDb;
    }
}
