package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

public interface IFindStudentsByGroup extends IPercentStudentsRegistrationOnCourses {
    String getGroupName();
    Long getStudyYear();
    String getDepartment();
    String getFacultyName();
    Long getCount();
}
