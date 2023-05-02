package ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.CourseSelectedByStudentsGroup;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.StudentNameAndId;

import java.util.List;
@Getter
@Setter
public class GroupStudentsRegistrationResultDTO {
    List<CourseSelectedByStudentsGroup> coursesSelectedByStudentsGroup;
    List<StudentNameAndId> groupStudentsWithNoSelectedCourses;
}
