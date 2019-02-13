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
    private List<DiplomaAndStudentSynchronizedDataBean> diplomaAndStudentSynchronizedDataGreen;
    private List<MissingDataBean> missingDataRed;

    public EdeboDiplomaNumberSynchronizationReport(){
        diplomaAndStudentSynchronizedDataGreen = new ArrayList();
        missingDataRed = new ArrayList();
    }

    public void addBeanToSynchronizedList(DiplomaAndStudentSynchronizedDataBean bean){
        diplomaAndStudentSynchronizedDataGreen.add(bean);
    }
    public void addBeanToMissingDataList(MissingDataBean bean){
        missingDataRed.add(bean);
    }
}
