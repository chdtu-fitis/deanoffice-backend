package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

public interface IFindStudentsByFaculty extends IPercentStudentsRegistrationOnCourses {
    Long getCount();
    String getFacultyName();
}
