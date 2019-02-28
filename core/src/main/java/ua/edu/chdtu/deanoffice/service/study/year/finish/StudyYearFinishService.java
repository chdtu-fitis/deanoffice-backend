package ua.edu.chdtu.deanoffice.service.study.year.finish;

import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentExpelService;

import java.util.Date;
import java.util.List;

public class StudyYearFinishService {
    private StudentDegreeService studentDegreeService;
    private StudentExpelService studentExpelService;

    @Autowired
    public StudyYearFinishService(StudentDegreeService studentDegreeService,
                                     StudentExpelService studentExpelService) {
        this.studentDegreeService = studentDegreeService;
        this.studentExpelService = studentExpelService;
    }

    public void expelStudents(List<Integer> ids, Date expelDate, Date orderDate, int orderNumber) {
        try {
            studentDegreeService.setStudentDegreesInactive(ids);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
