package ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentsRegistrationOnCoursesByFacultyAndCourseAndSpecializationPercentDTO {
    private String facultyName;
    private int studyYear;
    private String specializationName;
    private int percent;
}