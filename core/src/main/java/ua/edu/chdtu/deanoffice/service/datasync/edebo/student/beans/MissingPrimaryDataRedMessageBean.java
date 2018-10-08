package ua.edu.chdtu.deanoffice.service.datasync.edebo.student.beans;

import lombok.Getter;

@Getter
public class MissingPrimaryDataRedMessageBean {
    private StudentDegreePrimaryDataBean studentDegreePrimaryDataBean;
    private String message;

    public MissingPrimaryDataRedMessageBean(){}
    public MissingPrimaryDataRedMessageBean(String message, StudentDegreePrimaryDataBean studentDegreePrimaryDataBean){
        this.message = message;
        this.studentDegreePrimaryDataBean = studentDegreePrimaryDataBean;
    }
}
