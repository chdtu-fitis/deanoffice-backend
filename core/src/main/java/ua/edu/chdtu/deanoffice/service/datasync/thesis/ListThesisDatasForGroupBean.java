package ua.edu.chdtu.deanoffice.service.datasync.thesis;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListThesisDatasForGroupBean {
    String groupName;
    List<ThesisDataBean> thesisDataBeans;

    public ListThesisDatasForGroupBean(String groupName, List<ThesisDataBean> thesisDataBeans){
        this.groupName = groupName;
        this.thesisDataBeans = thesisDataBeans;
    }
}
