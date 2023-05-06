package ua.edu.chdtu.deanoffice.service.datasync.thesis.beans;

import lombok.Getter;
import ua.edu.chdtu.deanoffice.service.datasync.thesis.beans.ThesisDataBean;

@Getter
public class ThesisDataWithMessageBean {
    private ThesisDataBean thesisPrimaryData;
    private String message;

    public ThesisDataWithMessageBean(){}
    public ThesisDataWithMessageBean(String message, ThesisDataBean thesisPrimaryData){
        this.message = message;
        this.thesisPrimaryData = thesisPrimaryData;
    }
}
