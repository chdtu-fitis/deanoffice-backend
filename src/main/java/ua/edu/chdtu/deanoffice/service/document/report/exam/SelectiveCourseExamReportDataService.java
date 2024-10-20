package ua.edu.chdtu.deanoffice.service.document.report.exam;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.repository.FacultyRepository;
import ua.edu.chdtu.deanoffice.repository.SelectiveCourseRepository;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.ExamReportDataBean;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.GroupExamReportDataBean;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class SelectiveCourseExamReportDataService extends ExamReportDataBaseService {
    private final SelectiveCourseRepository selectiveCourseRepository;
    private final SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository;
    private SelectiveCourse currentSelectiveCourse;

    public SelectiveCourseExamReportDataService(SelectiveCourseRepository selectiveCourseRepository, CurrentYearService currentYearService,
                                                FacultyRepository facultyRepository,
                                                SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository) {
        super(currentYearService, facultyRepository);
        this.selectiveCourseRepository = selectiveCourseRepository;
        this.selectiveCoursesStudentDegreesRepository = selectiveCoursesStudentDegreesRepository;
    }

    public List<ExamReportDataBean> getExamReportData() {
        List<ExamReportDataBean> examReportDataBeans = new ArrayList<>();

        List<SelectiveCourse> selectiveCourses = selectiveCourseRepository.findByIdIn(this.getCourseIds());
        for (SelectiveCourse selectiveCourse : selectiveCourses) {
            this.currentSelectiveCourse = selectiveCourse;
            List<SelectiveCoursesStudentDegrees> coursesDegrees = selectiveCoursesStudentDegreesRepository.findActiveBySelectiveCourse(selectiveCourse.getId());
            List <StudentDegree> studentDegrees = coursesDegrees.stream()
                    .map(courseDegree -> courseDegree.getStudentDegree())
                    .filter(studentDegree -> studentDegree.getSpecialization().getFaculty().getId() == FacultyUtil.getUserFacultyIdInt())
//                    .filter(studentDegree -> studentDegree.getStudentGroup().getTuitionForm() == TuitionForm.FULL_TIME)
                    .collect(Collectors.toList());

            if (!studentDegrees.isEmpty()) {
                ExamReportDataBean examReportDataBean = createExamReportDataBean(createCourseBean(selectiveCourse),
                                                                                 createGroupBean(selectiveCourse),
                                                                                 createStudentsBean(studentDegrees));
                examReportDataBeans.add(examReportDataBean);
            }
        };

        return examReportDataBeans;
    }

    protected Integer getGroupSemester() {
        return this.currentSelectiveCourse.getCourse().getSemester();
    }

    protected String getExamDateFieldReplacement() {
        return "";
    }

    private GroupExamReportDataBean createGroupBean(SelectiveCourse selectiveCourse) {
        return createGroupBean(selectiveCourse.getDegree().getName(), selectiveCourse.getGroupName());
    }
}
