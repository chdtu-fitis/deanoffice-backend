package ua.edu.chdtu.deanoffice.service.datasync.thesis;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.service.datasync.thesis.beans.ListThesisDataForGroupBean;
import ua.edu.chdtu.deanoffice.service.datasync.thesis.beans.ThesisDataWithMessageBean;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ThesisReport {
    private List <ListThesisDataForGroupBean> thesisDataForImportGreen;
    private List <ThesisDataWithMessageBean> thesisDataWithMessageRed;

    public ThesisReport(){
        thesisDataForImportGreen = new ArrayList<>();
        thesisDataWithMessageRed = new ArrayList<>();
    }

    public void addThesisDataForImportToGreenList(ListThesisDataForGroupBean bean){
        thesisDataForImportGreen.add(bean);
    }

    public void addThesisWithMissingDataToRedList(ThesisDataWithMessageBean bean){
        thesisDataWithMessageRed.add(bean);
    }
}
