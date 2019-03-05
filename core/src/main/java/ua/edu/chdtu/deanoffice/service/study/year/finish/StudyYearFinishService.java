package ua.edu.chdtu.deanoffice.service.study.year.finish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentExpelService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class StudyYearFinishService {
    private StudentDegreeService studentDegreeService;
    private StudentExpelService studentExpelService;

    @Autowired
    public StudyYearFinishService(StudentDegreeService studentDegreeService,
                                     StudentExpelService studentExpelService) {
        this.studentDegreeService = studentDegreeService;
        this.studentExpelService = studentExpelService;
    }

    public void expelStudents(List<StudentDegree> studentDegrees, Date expelDate, Date orderDate, String orderNumber) throws Exception {
            //studentDegreeService.setStudentDegreesInactive(ids);
            //studentDegreeRepository.getAllByIds(ids);


            studentExpelService.expelStudents(studentDegrees, expelDate, orderDate, orderNumber);

            List<Integer> groups = new ArrayList<>();

            for (StudentDegree studentDegree : studentDegrees) {
                if (studentDegree.getStudentGroup().isActive() == true) {

                } else {
                    throw new OperationCannotBePerformedException("Студенти");
                }
            }
    }
}
