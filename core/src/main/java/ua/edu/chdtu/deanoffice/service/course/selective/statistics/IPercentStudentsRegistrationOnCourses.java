package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

public interface IPercentStudentsRegistrationOnCourses {
    int getStudyYear();
    String getName();
    String getFacultyName();
    String getGroupName();
    String getSpecializationName();
    String getDepartment();
    Long getPercent();
    Long getCount();

    void setPercent(int percent);
}
