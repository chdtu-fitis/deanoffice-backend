package ua.edu.chdtu.deanoffice.service.datasync.thesis;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ThesisReport {
    private List <ThesisDataBean> thesisGreen;
    private List <ThesisWithMessageRedBean> thesisRedMessage;

    public ThesisReport(){
        thesisGreen = new ArrayList<>();
        thesisRedMessage = new ArrayList<>();
    }

    public void addThesisGreen(ThesisDataBean bean){
        thesisGreen.add(bean);
    }

    public void addThesisRed(ThesisWithMessageRedBean bean){
        thesisRedMessage.add(bean);
    }



    public void clear(){
        thesisGreen.clear();
        thesisRedMessage.clear();
    }
}
