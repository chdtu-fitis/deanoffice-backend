package ua.edu.chdtu.deanoffice.service.document.report.exam;

import lombok.Getter;
import lombok.Setter;
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


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Service
public class RegularExamReportDataService extends ExamReportDataBaseService {
    private final CourseForGroupService courseForGroupService;
    private final StudentGroupService studentGroupService;
    private final StudentDegreeService studentDegreeService;
    private int studentGroupId;
    private StudentGroup studentGroup;
    private CourseForGroup currentCourseForGroup;


    public RegularExamReportDataService(CourseForGroupService courseForGroupService, CurrentYearService currentYearService,
                                                FacultyRepository facultyRepository, StudentGroupService studentGroupService, StudentDegreeService studentDegreeService) {
        super(currentYearService, facultyRepository);
        this.courseForGroupService = courseForGroupService;
        this.studentGroupService = studentGroupService;
        this.studentDegreeService = studentDegreeService;

    }

    public List<ExamReportDataBean> getExamReportData() {
        List<ExamReportDataBean> examReportDataBeans = new ArrayList<>();

        List<StudentDegree> studentDegrees = studentDegreeService.getAllByGroupId(studentGroupId);
        StudentGroup studentGroup = studentGroupService.getById(studentGroupId);
        this.studentGroup = studentGroup;
        String groupName = studentGroup.getName();
        String degreeName = studentGroup.getSpecialization().getDegree().getName();

        List<StudentExamReportDataBean> studentExamReportDataBeans = createStudentsBean(studentDegrees);

        List<CourseForGroup> coursesForGroup = getCoursesForGroup();
        for (CourseForGroup courseForGroup : coursesForGroup) {
            setCurrentCourseForGroup(courseForGroup);
            CourseExamReportDataBean courseExamReportDataBean = createCourseBean(courseForGroup);
            GroupExamReportDataBean groupExamReportDataBean = createGroupBean(degreeName, groupName);
            ExamReportDataBean examReportDataBean = createExamReportDataBean(courseExamReportDataBean, groupExamReportDataBean, studentExamReportDataBeans);
            examReportDataBeans.add(examReportDataBean);
        }

        return examReportDataBeans;
    }

    protected String getExamDateFieldReplacement() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        CourseForGroup courseForGroup = getCurrentCourseForGroup();
        if (courseForGroup.getExamDate() != null) {
            return dateFormat.format(courseForGroup.getExamDate());
        } else {
            return "";
        }
    }

    protected Integer getGroupSemester() {
        return this.studentGroup.getStudySemesters();
    }


    private List<CourseForGroup> getCoursesForGroup() {
        return getCourseIds().stream()
                .map(groupCourseId -> courseForGroupService.getCourseForGroup(getStudentGroupId(), groupCourseId))
                .collect(Collectors.toList());
    }
}
