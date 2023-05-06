package ua.edu.chdtu.deanoffice.api.course.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentCoursesDTO {
    List<StudentCourseDTO> firstSemesterCourses;
    List<StudentCourseDTO> secondSemesterCourses;
}

