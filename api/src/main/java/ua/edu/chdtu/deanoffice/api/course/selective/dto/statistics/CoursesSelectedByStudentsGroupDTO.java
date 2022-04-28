package ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CoursesSelectedByStudentsGroupDTO {
    int selectiveCourseId;
    int semester;
    String nameCourses;
    Map<String, Integer> RegisteredStudent;
}
