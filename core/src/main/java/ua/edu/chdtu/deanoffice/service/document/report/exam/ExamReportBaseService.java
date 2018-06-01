package ua.edu.chdtu.deanoffice.service.document.report.exam;

import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.util.PersonUtil.makeInitials;


public class ExamReportBaseService {

    private CurrentYearService currentYearService;

    @Autowired
    public ExamReportBaseService(CurrentYearService currentYearService) {
        this.currentYearService = currentYearService;
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

        result.put("Course", String.format("%d", Calendar.getInstance().get(Calendar.YEAR) - courseForGroup.getStudentGroup().getCreationYear()));
        result.put("KCType", course.getKnowledgeControl().getName());
        if (courseForGroup.getTeacher() != null) {
            result.put("TeacherName", courseForGroup.getTeacher().getFullNameUkr());
            result.put("TeacherInitials", courseForGroup.getTeacher().getInitialsUkr());
        }
        result.put("Semester", String.format("%d-й", courseForGroup.getCourse().getSemester()));

        return result;
    }

    Map<String, String> getGroupInfoReplacements(CourseForGroup courseForGroup) {
        Map<String, String> result = new HashMap<>();
        StudentGroup studentGroup = courseForGroup.getStudentGroup();
        result.put("GroupName", studentGroup.getName());
        Speciality speciality = studentGroup.getSpecialization().getSpeciality();
        result.put("Specialization", speciality.getCode() + " " + speciality.getName());
        result.put("FacultyAbbr", studentGroup.getSpecialization().getDepartment().getFaculty().getAbbr());
        result.put("DeanInitials", makeInitials(studentGroup.getSpecialization().getDepartment().getFaculty().getDean()));
        result.put("Degree", studentGroup.getSpecialization().getDegree().getName());
        result.put("StudyYear", getStudyYear());

        return result;
    }

    private String getStudyYear() {
        int currentYear = currentYearService.get().getCurrYear();
        return String.format("%4d-%4d", currentYear, currentYear + 1);
    }
}
