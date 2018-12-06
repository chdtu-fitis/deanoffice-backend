package ua.edu.chdtu.deanoffice.service.datasync.thesis;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.service.datasync.thesis.beans.ListThesisDatasForGroupBean;
import ua.edu.chdtu.deanoffice.service.datasync.thesis.beans.RedThesisWithMessageBean;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ThesisReport {
    private List <ListThesisDatasForGroupBean> thesisGreen;
    private List <RedThesisWithMessageBean> thesisRedWithMessage;

    public ThesisReport(){
        thesisGreen = new ArrayList<>();
        thesisRedWithMessage = new ArrayList<>();
    }

    public void addThesisGreen(ListThesisDatasForGroupBean bean){
        thesisGreen.add(bean);
    }

    public void addThesisRed(RedThesisWithMessageBean bean){
        thesisRedWithMessage.add(bean);
    }
}
