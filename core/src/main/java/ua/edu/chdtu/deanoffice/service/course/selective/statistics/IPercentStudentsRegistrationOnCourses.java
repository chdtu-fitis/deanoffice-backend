package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import java.util.List;
import java.util.Map;

public interface IPercentStudentsRegistrationOnCourses {
    int getStudyYear();
    String getName();
    String getFacultyName();
    String getGroupName();
    String getSpecializationName();
    String getDepartment();
    Long getPercent();
    Long getRegisteredCount();
    Long getTotalCount();
    Long getPartRegisteredCount();
    Long getPartRegisteredPercent();
    Long getNotRegisteredCount();
    Long getNotRegisteredPercent();
    int getStudentId();

    void setPercent(int percent);
    void setRegisteredCount(int registeredCount);
}
