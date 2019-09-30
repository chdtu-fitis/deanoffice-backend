package ua.edu.chdtu.deanoffice.service.document.report.qualificationstatement;

import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

public class QualificationWorkService {
    private StudentGroupService studentGroupService;

    @Autowired
    public QualificationWorkService(StudentGroupService studentGroupService) {
        this.studentGroupService = studentGroupService;
    }

}
