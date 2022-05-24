package ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentsRegistrationOnCoursesByFacultyAndCoursesPercentDTO {
    private String facultyName;
    private int studyYear;
    private int totalCount;
    private int registeredCount;
    private int percent;
}
