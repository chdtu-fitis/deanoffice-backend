package ua.edu.chdtu.deanoffice.service.datasync.thesis.beans;

import lombok.Getter;
import ua.edu.chdtu.deanoffice.service.datasync.thesis.beans.ThesisDataBean;

@Getter
public class RedThesisWithMessageBean {
    private ThesisDataBean thesisPrimaryData;
    private String message;

    public RedThesisWithMessageBean(){}
    public RedThesisWithMessageBean(String message, ThesisDataBean thesisPrimaryData){
        this.message = message;
        this.thesisPrimaryData = thesisPrimaryData;
    }
}
