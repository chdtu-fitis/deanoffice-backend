package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

public interface IPercentStudentsRegistrationOnCourses {
    Long getStudyYear();
    String getName();
    String getFacultyName();
    String getGroupName();
    String getSpecializationName();
    String getDepartment();
    Long getRegisteredPercent();
    Long getRegisteredCount();
    Long getTotalCount();
    Long getCount();
    Long getChoosingLessCount();
    Long getChoosingLessPercent();
    Long getNotRegisteredCount();
    Long getNotRegisteredPercent();
    int getStudentId();

    void setRegisteredPercent(int percent);
    void setRegisteredCount(int registeredCount);
    void setChoosingLessPercent(int percent);
    void setChoosingLessCount(int registeredCount);
    void setNotRegisteredPercent(int percent);
    void setNotRegisteredCount(int registeredCount);
    void setCount(int registeredCount);
}
