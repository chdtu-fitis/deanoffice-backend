package ua.edu.chdtu.deanoffice.service.datasync.edebo.diploma.number;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.diploma.number.beans.DiplomaAndStudentSynchronizedDataBean;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.diploma.number.beans.MissingDataBean;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EdeboDiplomaNumberSynchronizationReport {
    private List<DiplomaAndStudentSynchronizedDataBean> diplomaAndStudentSynchronizedDataBeans;
    private List<MissingDataBean> missingDataBeans;

    public EdeboDiplomaNumberSynchronizationReport(){
        diplomaAndStudentSynchronizedDataBeans = new ArrayList();
        missingDataBeans = new ArrayList();
    }

    public void addBeanToSynchronizedList(DiplomaAndStudentSynchronizedDataBean bean){
        diplomaAndStudentSynchronizedDataBeans.add(bean);
    }
    public void addBeanToMissingDataList(MissingDataBean bean){
        missingDataBeans.add(bean);
    }
}
