package ua.edu.chdtu.deanoffice.service.study.year.finish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentExpelService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.util.*;

@Service
public class StudyYearFinishService {
    private StudentDegreeService studentDegreeService;
    private StudentExpelService studentExpelService;
    private StudentGroupService studentGroupService;

    @Autowired
    public StudyYearFinishService(StudentDegreeService studentDegreeService,
                                     StudentExpelService studentExpelService,
                                     StudentGroupService studentGroupService) {
        this.studentDegreeService = studentDegreeService;
        this.studentExpelService = studentExpelService;
        this.studentGroupService = studentGroupService;
    }

    public Set<StudentGroup> expelStudentsAndDisableGroups(List<StudentDegree> studentDegrees, Date expelDate, Date orderDate, String orderNumber) throws Exception {
            studentExpelService.expelStudents(studentDegrees, expelDate, orderDate, orderNumber);

            Set<StudentGroup> groups = new HashSet<>();
            Set<Integer> groupIds = new HashSet<>();
            for (StudentDegree studentDegree : studentDegrees) {
                if (studentDegree.getStudentGroup().isActive() == true) {
                    if (studentDegree.getStudentGroup().getStudentDegrees().size() == 0) {
                        groups.add(studentDegree.getStudentGroup());
                        groupIds.add(studentDegree.getStudentGroup().getId());
                    }
                }
            }

            if (groups.size() != 0) {
                studentGroupService.setStudentGroupsInactiveByIds(groupIds);
            }

            return groups;
    }
}
