package ua.edu.chdtu.deanoffice.service.datasync.edebo.diploma.number.beans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissingDataBean {
    private String message;
    private DiplomaAndStudentSynchronizedDataBean diplomaAndStudentSynchronizedDataBean;

    public MissingDataBean(String message, DiplomaAndStudentSynchronizedDataBean diplomaAndStudentSynchronizedDataBean){
        this.message = message;
        this.diplomaAndStudentSynchronizedDataBean = diplomaAndStudentSynchronizedDataBean;
    }
}
