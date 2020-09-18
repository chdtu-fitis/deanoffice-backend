package ua.edu.chdtu.deanoffice.api.selective.courses.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.teacher.TeacherDTO;

@Getter
@Setter
public class SelectiveCourseDTO {
    private Integer id;
    private boolean available;
    private CourseDTO course;
    private TeacherDTO teacher;
    private Integer studyYear;

}
