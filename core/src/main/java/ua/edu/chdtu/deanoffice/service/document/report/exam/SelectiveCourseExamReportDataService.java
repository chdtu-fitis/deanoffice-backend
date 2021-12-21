package ua.edu.chdtu.deanoffice.service.document.report.exam;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.repository.FacultyRepository;
import ua.edu.chdtu.deanoffice.repository.SelectiveCourseRepository;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.CourseExamReportDataBean;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.ExamReportDataBean;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.GroupExamReportDataBean;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.StudentExamReportDataBean;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;
import ua.edu.chdtu.deanoffice.util.PersonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.util.PersonUtil.makeNameThenSurnameInCapital;

@Service
public class SelectiveCourseExamReportDataService {
    private SelectiveCourseRepository selectiveCourseRepository;
    private CurrentYearService currentYearService;
    private FacultyRepository facultyRepository;
    private SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository;

    public SelectiveCourseExamReportDataService(SelectiveCourseRepository selectiveCourseRepository, CurrentYearService currentYearService,
                                                FacultyRepository facultyRepository,
                                                SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository) {
        this.selectiveCourseRepository = selectiveCourseRepository;
        this.currentYearService = currentYearService;
        this.facultyRepository = facultyRepository;
        this.selectiveCoursesStudentDegreesRepository = selectiveCoursesStudentDegreesRepository;
    }

    public List<ExamReportDataBean> getExamReportData(List<Integer> selectiveCourseIds) {
        List<ExamReportDataBean> examReportDataBeans = new ArrayList<>();

        List<SelectiveCourse> selectiveCourses = selectiveCourseRepository.findByIdIn(selectiveCourseIds);
        for (SelectiveCourse selectiveCourse : selectiveCourses) {
            List<SelectiveCoursesStudentDegrees> coursesDegrees = selectiveCoursesStudentDegreesRepository.findActiveBySelectiveCourse(selectiveCourse.getId());
            List <StudentDegree> studentDegrees = coursesDegrees.stream()
                    .map(courseDegree -> courseDegree.getStudentDegree())
                    .filter(studentDegree -> studentDegree.getSpecialization().getFaculty().getId() == FacultyUtil.getUserFacultyIdInt())
                    .collect(Collectors.toList());
            if (studentDegrees.size() > 0) {
                ExamReportDataBean examReportDataBean = new ExamReportDataBean();
                examReportDataBean.setCourseExamReportDataBean(createCourseBean(selectiveCourse));
                examReportDataBean.setGroupExamReportDataBean(createGroupBean(selectiveCourse));
                examReportDataBean.setStudentExamReportDataBeans(createStudentsBean(studentDegrees));
                examReportDataBeans.add(examReportDataBean);
            }
        };
        return examReportDataBeans;
    }

    private CourseExamReportDataBean createCourseBean(SelectiveCourse selectiveCourse) {
        CourseExamReportDataBean courseDataBean = new CourseExamReportDataBean();
        courseDataBean.setCourseName(selectiveCourse.getCourse().getCourseName().getName());
        courseDataBean.setExamDate("");
        courseDataBean.setHours("" + selectiveCourse.getCourse().getHours());
        courseDataBean.setKnowledgeControlName(selectiveCourse.getCourse().getKnowledgeControl().getName());
        courseDataBean.setSemester("" + selectiveCourse.getCourse().getSemester());
        if (selectiveCourse.getTeacher() != null) {
            Teacher teacher = selectiveCourse.getTeacher();
            String teacherTitle = teacher.getAcademicTitle() != null ? teacher.getAcademicTitle().getNameUkr() : "";
            courseDataBean.setTeacherName(teacherTitle + " " + teacher.getSurname() + " " + teacher.getName() + " " + teacher.getPatronimic());
            courseDataBean.setTeacherInitials(makeNameThenSurnameInCapital(teacher.getName(), teacher.getSurname()));
        }
        String academicYear = ""+selectiveCourse.getStudyYear();
        courseDataBean.setYearShort(academicYear.substring(academicYear.length() - 2));
        return courseDataBean;
    }

    private GroupExamReportDataBean createGroupBean(SelectiveCourse selectiveCourse) {
        GroupExamReportDataBean groupDataBean = new GroupExamReportDataBean();
        groupDataBean.setAcademicYear(getStudyYear());
        Faculty faculty = facultyRepository.findById(FacultyUtil.getUserFacultyIdInt());
        groupDataBean.setDeanInitials(PersonUtil.makeNameThenSurnameInCapital(faculty.getDean()));
        groupDataBean.setDegreeName(selectiveCourse.getDegree().getName());
        groupDataBean.setFacultyAbbr(faculty.getAbbr());
        groupDataBean.setGroupName(selectiveCourse.getGroupName());
        groupDataBean.setGroupStudyYear("" + (selectiveCourse.getCourse().getSemester() + 1) / 2);
        return groupDataBean;
    }

    private List<StudentExamReportDataBean> createStudentsBean(List<StudentDegree> studentDegrees) {
        List<StudentExamReportDataBean> studentExamReportDataBeans = new ArrayList<>();
        studentDegrees.forEach(sd -> {
            Student student = sd.getStudent();
            StudentExamReportDataBean bean = new StudentExamReportDataBean(student.getSurname(), student.getName(), student.getPatronimic(), sd.getRecordBookNumber());
            studentExamReportDataBeans.add(bean);
        });
        return studentExamReportDataBeans;
    }

    private String getStudyYear() {
        int currentYear = currentYearService.get().getCurrYear();
        return String.format("%4d-%4d", currentYear, currentYear + 1);
    }
}
