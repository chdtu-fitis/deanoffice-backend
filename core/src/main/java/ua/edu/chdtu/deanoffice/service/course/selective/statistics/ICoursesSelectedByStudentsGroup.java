package ua.edu.chdtu.deanoffice.service.course.selective.statistics;


public interface ICoursesSelectedByStudentsGroup {
    int getSelectiveCourseId();
    int getStudentDegreeId();
    int getSemester();
    String getFieldOfKnowledgeCode();
    String getTrainingCycle();
    String getStudentFullName();
    String getCourseName();
}
