package ua.edu.chdtu.deanoffice.service.document.report.exam;

import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.CourseExamReportDataBean;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.GroupExamReportDataBean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.util.PersonUtil.makeNameThenSurnameInCapital;

public class ExamReportBaseService {

    private CurrentYearService currentYearService;
    private static final int EXAMS_AND_CREDITS_INDEX = 0, COURSE_PAPERS_INDEX = 1, INTERNSHIPS_INDEX = 2;

    @Autowired
    public ExamReportBaseService(CurrentYearService currentYearService) {
        this.currentYearService = currentYearService;
    }

    Map<String, String> getCourseInfoReplacements(CourseExamReportDataBean course) {
        Map<String, String> result = new HashMap<>();
        result.put("CourseName", course.getCourseName());
        result.put("Hours", course.getHours());
        result.put("ExamDate", course.getExamDate());
        int dbCurrentYear = currentYearService.get().getCurrYear();
        int currentYear = dbCurrentYear + 1 - (Integer.parseInt(course.getSemester()) % 2);
        result.put("Year", String.valueOf(currentYear).substring(2));
        result.put("KCType", course.getKnowledgeControlName());
        result.put("TeacherName", course.getTeacherName());
        result.put("TeacherInitials", course.getTeacherInitials());
        result.put("Semester", course.getSemester() + "-й");
        return result;
    }

    Map<String, String> getGroupInfoReplacements(GroupExamReportDataBean group) {
        Map<String, String> result = new HashMap<>();
        result.put("GroupName", group.getGroupName());
        result.put("Speciality", group.getSpeciality());
        result.put("Specialization", group.getSpecializationName());
        result.put("FacultyAbbr", group.getFacultyAbbr());
        result.put("DeanInitials", group.getDeanInitials());
        result.put("Degree", group.getDegreeName());
        result.put("Course", group.getGroupStudyYear());
        result.put("StudyYear", group.getAcademicYear());
        return result;
    }

    Map<String, String> getCourseInfoReplacements(CourseForGroup courseForGroup) {
        Course course = courseForGroup.getCourse();
        Map<String, String> result = new HashMap<>();
        result.put("CourseName", course.getCourseName().getName());
        result.put("Hours", String.format("%d", course.getHours()));

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        if (courseForGroup.getExamDate() != null) {
            result.put("ExamDate", dateFormat.format(courseForGroup.getExamDate()));
        } else {
            result.put("ExamDate", "");
        }

        int dbCurrentYear = currentYearService.get().getCurrYear();
        int currentYear = dbCurrentYear + 1 - (course.getSemester() % 2);
        result.put("Year", String.valueOf(currentYear).substring(2));
        result.put("KCType", course.getKnowledgeControl().getName());
        Teacher teacher = courseForGroup.getTeacher();
        if (teacher != null) {
            String teacherTitle = teacher.getAcademicTitle() != null ? teacher.getAcademicTitle().getNameUkr() : "";
            result.put("TeacherName", teacherTitle + " " + teacher.getSurname() + " " + teacher.getName() + " " + teacher.getPatronimic());
            result.put("TeacherInitials", makeNameThenSurnameInCapital(teacher.getName(), teacher.getSurname()));
        }
        result.put("Semester", String.format("%d-й", courseForGroup.getCourse().getSemester()));
        return result;
    }

    Map<String, String> getGroupInfoReplacements(CourseForGroup courseForGroup) {
        Map<String, String> result = new HashMap<>();
        StudentGroup studentGroup = courseForGroup.getStudentGroup();
        result.put("GroupName", studentGroup.getName());
        Speciality speciality = studentGroup.getSpecialization().getSpeciality();
        result.put("Speciality", speciality.getCode() + " " + speciality.getName());
        result.put("Specialization", studentGroup.getSpecialization().getName());
        result.put("FacultyAbbr", studentGroup.getSpecialization().getFaculty().getAbbr());
        result.put("DeanInitials", makeNameThenSurnameInCapital(studentGroup.getSpecialization().getFaculty().getDean()));
        result.put("Degree", studentGroup.getSpecialization().getDegree().getName());
        int dbCurrentYear = currentYearService.get().getCurrYear();
        result.put("Course", String.format("%d", dbCurrentYear - courseForGroup.getStudentGroup().getCreationYear() + courseForGroup.getStudentGroup().getBeginYears()));
        result.put("StudyYear", getStudyYear());
        return result;
    }

    Map<String, String> getGroupInfoReplacements(List<StudentGroup> studentGroups, ApplicationUser user) {
        Map<String, String> result = new HashMap<>();
        result.put("ReportType", "ЗВЕДЕНА");
        String groupNames = "";
        for (StudentGroup studentGroup : studentGroups) {
            groupNames += studentGroup.getName() + ",";
        }
        if (groupNames.length() > 0) {
            groupNames = groupNames.substring(0, groupNames.length()-1);
        }
        result.put("GroupName", groupNames);
        Speciality speciality = studentGroups.get(0).getSpecialization().getSpeciality();
        for (StudentGroup studentGroup : studentGroups) {
            if (studentGroup.getSpecialization().getSpeciality().getId() != speciality.getId()) {
                speciality = null;
                break;
            }
        }

        result.put("Speciality", speciality == null ? "" : speciality.getCode() + " " + speciality.getName());

        Specialization specialization = studentGroups.get(0).getSpecialization();
        for (StudentGroup studentGroup : studentGroups) {
            if (studentGroup.getSpecialization().getId() != specialization.getId()) {
                specialization = null;
                break;
            }
        }

        result.put("Specialization", specialization == null ? "" : specialization.getName());
        result.put("FacultyAbbr", user.getFaculty().getAbbr().toUpperCase());
        result.put("DeanInitials", makeNameThenSurnameInCapital(user.getFaculty().getDean()));
        result.put("Degree", studentGroups.get(0).getSpecialization().getDegree().getName());
        result.put("StudyYear", getStudyYear());

        return result;
    }

    protected String getStudyYear() {
        int currentYear = currentYearService.get().getCurrYear();
        return String.format("%4d-%4d", currentYear, currentYear + 1);
    }
}
