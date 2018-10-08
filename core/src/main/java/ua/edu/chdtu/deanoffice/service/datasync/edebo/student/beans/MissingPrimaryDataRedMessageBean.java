package ua.edu.chdtu.deanoffice.service.datasync.edebo.student.beans;

import lombok.Getter;

@Getter
public class MissingPrimaryDataRedMessageBean {
    private StudentDegreePrimaryDataBean studentDegreePrimaryData;
    private String message;

    public MissingPrimaryDataRedMessageBean(){}
    public MissingPrimaryDataRedMessageBean(String message, StudentDegreePrimaryDataBean studentDegreePrimaryData){
        this.message = message;
        this.studentDegreePrimaryData = studentDegreePrimaryData;
    }
}
