package ua.edu.chdtu.deanoffice.api.student.synchronization;

public class ThesisWithMessageRedBean {
    private ThesisDataBean thesisPrimaryData;
    private String message;

    public ThesisWithMessageRedBean(){}
    public ThesisWithMessageRedBean(String message, ThesisDataBean thesisPrimaryData){
        this.message = message;
        this.thesisPrimaryData = thesisPrimaryData;
    }
}
