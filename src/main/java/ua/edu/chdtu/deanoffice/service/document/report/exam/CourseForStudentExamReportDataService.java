package ua.edu.chdtu.deanoffice.service.document.report.exam;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.repository.FacultyRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.course.CoursesForStudentsService;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.CourseExamReportDataBean;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.ExamReportDataBean;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.GroupExamReportDataBean;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.StudentExamReportDataBean;


import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Service
public class CourseForStudentExamReportDataService extends ExamReportDataBaseService {
    private static final Logger log = LoggerFactory.getLogger(CourseForStudentExamReportDataService.class);
    private final CoursesForStudentsService coursesForStudentsService;
    private StudentGroup studentGroup;


    public CourseForStudentExamReportDataService(CurrentYearService currentYearService,
                                        FacultyRepository facultyRepository, CoursesForStudentsService coursesForStudentsService) {
        super(currentYearService, facultyRepository);
        this.coursesForStudentsService = coursesForStudentsService;
    }

    public List<ExamReportDataBean> getExamReportData() throws Exception {
        List<ExamReportDataBean> examReportDataBeans = new ArrayList<>();

        try {
            List<CourseForStudent> coursesForStudents = coursesForStudentsService.getByIds(getCourseIds());
            List<List<CourseForStudent>> groupedCourses = groupCoursesForStudents(coursesForStudents);

            for (List<CourseForStudent> group : groupedCourses) {
                List<StudentDegree> studentDegreesForCourse = group.stream()
                        .map(CourseForStudent::getStudentDegree)
                        .collect(Collectors.toList());

                    StudentGroup studentGroup = getStudentGroup(studentDegreesForCourse);
                    setStudentGroup(studentGroup);
                    String groupName = studentGroup.getName();
                    String degreeName = studentGroup.getSpecialization().getDegree().getName();

                    List<StudentExamReportDataBean> studentExamReportDataBeansForCourse = createStudentsBean(studentDegreesForCourse);
                    CourseExamReportDataBean courseExamReportDataBean = createCourseBean(group.get(0));
                    GroupExamReportDataBean groupExamReportDataBean = createGroupBean(degreeName, groupName);
                    ExamReportDataBean examReportDataBean = createExamReportDataBean(courseExamReportDataBean, groupExamReportDataBean, studentExamReportDataBeansForCourse);
                    examReportDataBeans.add(examReportDataBean);
            }
        } catch (Exception e) {
            log.error("Error getting exam report data: {}", e.getMessage());
            throw new OperationCannotBePerformedException("Error getting exam report data");
        }

        return examReportDataBeans;
    }

    private List<List<CourseForStudent>> groupCoursesForStudents(List<CourseForStudent> coursesForStudents) {
        List<List<CourseForStudent>> groupedCourses = new ArrayList<>();

        for (CourseForStudent course : coursesForStudents) {
            boolean found = false;
            for (List<CourseForStudent> group : groupedCourses) {
                if (group.get(0).equalsByCourseAndTeacher(course)) {
                    group.add(course);
                    found = true;
                    break;
                }
            }
            if (!found) {
                groupedCourses.add(new ArrayList<>(Collections.singletonList(course)));
            }
        }

        return groupedCourses;
    }

    private StudentGroup getStudentGroup(List<StudentDegree> studentDegrees) throws OperationCannotBePerformedException {
        try{
            StudentGroup studentGroup = studentDegrees.stream()
                .map(StudentDegree::getStudentGroup)
                .distinct()
                .reduce((a, b) -> {
                    throw new IllegalArgumentException("All students must be from the same group.");
                })
                .orElseThrow(() -> new IllegalArgumentException("No student degrees found."));
        }
        catch (IllegalArgumentException e) {
            throw new OperationCannotBePerformedException(e.getMessage());
        }

        return studentGroup;
    }

    protected Integer getGroupSemester() {
        return getStudentGroup().getStudySemesters();
    }

    protected String getExamDateFieldReplacement() {
        return "Академрізниця";
    }
}
