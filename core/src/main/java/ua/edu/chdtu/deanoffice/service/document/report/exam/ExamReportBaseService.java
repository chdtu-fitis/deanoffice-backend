package ua.edu.chdtu.deanoffice.service.document.report.exam;

import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.util.PersonUtil.makeInitialsSurnameLast;


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

        result.put("Course", String.format("%d", currentYearService.get().getCurrYear() - courseForGroup.getStudentGroup().getCreationYear() + 1));
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
        result.put("Speciality", speciality.getCode() + " " + speciality.getName());
        result.put("Specialization", studentGroup.getSpecialization().getName());
        result.put("FacultyAbbr", studentGroup.getSpecialization().getFaculty().getAbbr());
        result.put("DeanInitials", makeInitialsSurnameLast(studentGroup.getSpecialization().getFaculty().getDean()));
        result.put("Degree", studentGroup.getSpecialization().getDegree().getName());
        result.put("StudyYear", getStudyYear());

        return result;
    }

    Map<String, String> getGroupInfoReplacements(List<StudentGroup> studentGroups, ApplicationUser user) {
        Map<String, String> result = new HashMap<>();
        String groupNames = "";
        for (StudentGroup studentGroup : studentGroups) {
            groupNames += studentGroup.getName() + ",";
        }
        //if (groupNames != null)
            //groupNames.substring(groupNames.length() - 2);
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
        result.put("DeanInitials", user.getFaculty().getDean());
        result.put("Degree", studentGroups.get(0).getSpecialization().getDegree().getName());
        result.put("StudyYear", getStudyYear());

        return result;
    }

    private String getStudyYear() {
        int currentYear = currentYearService.get().getCurrYear();
        return String.format("%4d-%4d", currentYear, currentYear + 1);
    }
}
