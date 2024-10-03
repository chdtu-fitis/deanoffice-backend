package ua.edu.chdtu.deanoffice.api.course.dto.coursesforstudents;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.teacher.TeacherDTO;
import ua.edu.chdtu.deanoffice.entity.CourseType;

@Getter
@Setter
public class CourseForStudentDTO {
    private int id;
    private CourseDTO course;
    private StudentDegreeDTO studentDegree;
    private TeacherDTO teacher;
    private CourseType courseType;
}
