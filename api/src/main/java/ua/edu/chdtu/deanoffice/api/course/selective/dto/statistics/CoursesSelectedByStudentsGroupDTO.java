package ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoursesSelectedByStudentsGroupDTO {
    String studentSurname;
    String studentName;
    String nameCourses;
}
