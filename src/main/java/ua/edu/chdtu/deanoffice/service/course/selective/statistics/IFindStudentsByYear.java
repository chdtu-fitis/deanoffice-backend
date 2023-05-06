package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

public interface IFindStudentsByYear extends IPercentStudentsRegistrationOnCourses {
    Long getCount();
    Long getStudyYear();
}
