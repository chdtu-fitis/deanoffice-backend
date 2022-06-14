package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

public interface IFindStudentsByFacultyAndSpecialization extends IPercentStudentsRegistrationOnCourses {
    String getSpecializationName();
    String getFacultyName();
    Long getCount();
}
