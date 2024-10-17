package ua.edu.chdtu.deanoffice.service.document.report.exam;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.repository.FacultyRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.CourseExamReportDataBean;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.ExamReportDataBean;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.GroupExamReportDataBean;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.StudentExamReportDataBean;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class RegularExamReportDataService extends ExamReportDataBaseService {
    private final CourseForGroupService courseForGroupService;
    private final StudentGroupService studentGroupService;
    private final StudentDegreeService studentDegreeService;


    public List<ExamReportDataBean> getExamReportData() {
        return null;
    }

    public RegularExamReportDataService(CourseForGroupService courseForGroupService, CurrentYearService currentYearService,
                                                FacultyRepository facultyRepository, StudentGroupService studentGroupService, StudentDegreeService studentDegreeService) {
        super(currentYearService, facultyRepository);
        this.courseForGroupService = courseForGroupService;
        this.studentGroupService = studentGroupService;
        this.studentDegreeService = studentDegreeService;

    }

    public List<ExamReportDataBean> getExamReportData(List<Integer> baseCourseIds, int studentGroupId) {
        List<ExamReportDataBean> examReportDataBeans = new ArrayList<>();

        List<StudentDegree> studentDegrees = studentDegreeService.getAllByGroupId(studentGroupId);
        StudentGroup studentGroup = studentGroupService.getById(studentGroupId);
        String groupName = studentGroup.getName();
        String degreeName = studentDegrees.get(1).getSpecialization().getDegree().getName();

        List<StudentExamReportDataBean> studentExamReportDataBeans = createStudentsBean(studentDegrees);

        List<CourseForGroup> coursesForGroup = getCoursesForGroup(baseCourseIds, studentGroupId);
        for (CourseForGroup courseForGroup : coursesForGroup) {
            CourseExamReportDataBean courseExamReportDataBean = createCourseBean(courseForGroup);
            GroupExamReportDataBean groupExamReportDataBean = createGroupBean(courseForGroup, degreeName, groupName);
            ExamReportDataBean examReportDataBean = createExamReportDataBean(courseExamReportDataBean, groupExamReportDataBean, studentExamReportDataBeans);
            examReportDataBeans.add(examReportDataBean);
        }

        return examReportDataBeans;
    }


    private List<CourseForGroup> getCoursesForGroup(List<Integer> baseCourseIds, int studentGroupId) {
        return baseCourseIds.stream()
                .map(baseCourseId -> courseForGroupService.getCourseForGroup(studentGroupId, baseCourseId))
                .collect(Collectors.toList());
    }




}
