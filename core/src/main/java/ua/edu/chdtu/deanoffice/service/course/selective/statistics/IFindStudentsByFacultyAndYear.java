package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

public interface IFindStudentsByFacultyAndYear extends IPercentStudentsRegistrationOnCourses {
    String getFacultyName();
    Long getCount();
    Long getStudyYear();
}
