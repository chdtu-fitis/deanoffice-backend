package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

public interface IStudentsNotRightSelectiveCoursesNumber {
    int getStudentDegreeId();
    String getName();
    String getSurname();
    String getFacultyName();
    String getSpecialityCode();
    int getYear();
    String getGroup();
    int getCoursesNumber();
}
