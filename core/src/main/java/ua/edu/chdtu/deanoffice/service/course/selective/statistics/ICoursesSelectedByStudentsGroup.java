package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import java.util.Map;

public interface ICoursesSelectedByStudentsGroup {
    int getSelectiveCourseId();
    int getStudentDegreeId();
    int getSemester();
    String getStudentFullName();
    String getCourseName();
    Map<String, Integer> getRegisteredStudent();

    void setRegisteredStudent(Map<String, Integer> RegisteredStudent);
}
