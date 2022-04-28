package ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentsRegistrationOnCoursesByFacultyAndSpecializationPercentDTO {
    private String facultyName;
    private String specializationName;
    private int percent;
}
