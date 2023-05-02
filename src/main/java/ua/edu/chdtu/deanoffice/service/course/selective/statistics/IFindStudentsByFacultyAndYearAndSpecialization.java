package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

public interface IFindStudentsByFacultyAndYearAndSpecialization extends IPercentStudentsRegistrationOnCourses {
    String getSpecializationName();
    String getFacultyName();
    Long getStudyYear();
    Long getCount();
}
