package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

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

    void setPercent(int percent);
    void setRegisteredCount(int registeredCount);
}
