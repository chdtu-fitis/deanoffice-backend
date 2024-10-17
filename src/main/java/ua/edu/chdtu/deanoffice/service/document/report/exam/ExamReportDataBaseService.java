package ua.edu.chdtu.deanoffice.service.document.report.exam;

import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseCourse;
import ua.edu.chdtu.deanoffice.repository.FacultyRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.CourseExamReportDataBean;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.ExamReportDataBean;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.GroupExamReportDataBean;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.StudentExamReportDataBean;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;
import ua.edu.chdtu.deanoffice.util.PersonUtil;

import java.util.ArrayList;
import java.util.List;

import static ua.edu.chdtu.deanoffice.util.PersonUtil.makeNameThenSurnameInCapital;

public abstract class ExamReportDataBaseService {
    private final CurrentYearService currentYearService;
    private final FacultyRepository facultyRepository;

    public ExamReportDataBaseService(CurrentYearService currentYearService,
                                        FacultyRepository facultyRepository) {
        this.currentYearService = currentYearService;
        this.facultyRepository = facultyRepository;
    }


    protected ExamReportDataBean createExamReportDataBean(CourseExamReportDataBean courseExamReportDataBean,
                                                        GroupExamReportDataBean groupExamReportDataBean,
                                                        List<StudentExamReportDataBean> studentExamReportDataBeans) {
        ExamReportDataBean examReportDataBean = new ExamReportDataBean();
        examReportDataBean.setCourseExamReportDataBean(courseExamReportDataBean);
        examReportDataBean.setGroupExamReportDataBean(groupExamReportDataBean);
        examReportDataBean.setStudentExamReportDataBeans(studentExamReportDataBeans);

        return examReportDataBean;
    }


    protected CourseExamReportDataBean createCourseBean(BaseCourse baseCourse) {
        CourseExamReportDataBean courseDataBean = new CourseExamReportDataBean();
        courseDataBean.setCourseName(baseCourse.getCourse().getCourseName().getName());
        courseDataBean.setExamDate("");
        courseDataBean.setHours("" + baseCourse.getCourse().getHours());
        courseDataBean.setKnowledgeControlName(baseCourse.getCourse().getKnowledgeControl().getName());
        courseDataBean.setSemester("" + baseCourse.getCourse().getSemester());

        if (baseCourse.getTeacher() != null) {
            Teacher teacher = baseCourse.getTeacher();
            String teacherTitle = teacher.getAcademicTitle() != null ? teacher.getAcademicTitle().getNameUkr() : "";
            courseDataBean.setTeacherName(teacherTitle + " " + teacher.getSurname() + " " + teacher.getName() + " " + teacher.getPatronimic());
            courseDataBean.setTeacherInitials(makeNameThenSurnameInCapital(teacher.getName(), teacher.getSurname()));
        }

        return courseDataBean;
    }

    protected GroupExamReportDataBean createGroupBean(BaseCourse baseCourse, String degreeName, String groupName) {
        GroupExamReportDataBean groupDataBean = new GroupExamReportDataBean();
        groupDataBean.setAcademicYear(getStudyYear());
        Faculty faculty = facultyRepository.findById(FacultyUtil.getUserFacultyIdInt()).get();
        groupDataBean.setDeanInitials(PersonUtil.makeNameThenSurnameInCapital(faculty.getDean()));
        groupDataBean.setDegreeName(degreeName);
        groupDataBean.setFacultyAbbr(faculty.getAbbr());
        groupDataBean.setGroupName(groupName);
        groupDataBean.setGroupStudyYear(getGroupCourse(baseCourse));
        return groupDataBean;
    }

    private String getStudyYear() {
        int currentYear = currentYearService.get().getCurrYear();
        return String.format("%4d-%4d", currentYear, currentYear + 1);
    }

    private String getGroupCourse(BaseCourse baseCourse) {
        return "" + (baseCourse.getCourse().getSemester() + 1) / 2;
    }

    protected List<StudentExamReportDataBean> createStudentsBean(List<StudentDegree> studentDegrees) {
        List<StudentExamReportDataBean> studentExamReportDataBeans = new ArrayList<>();
        studentDegrees.forEach(sd -> {
            Student student = sd.getStudent();
            StudentExamReportDataBean bean = new StudentExamReportDataBean(student.getSurname(), student.getName(), student.getPatronimic(), sd.getRecordBookNumber());
            studentExamReportDataBeans.add(bean);
        });
        return studentExamReportDataBeans;
    }
}
