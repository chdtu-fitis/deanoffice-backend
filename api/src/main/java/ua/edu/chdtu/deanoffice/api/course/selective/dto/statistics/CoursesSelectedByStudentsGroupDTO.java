package ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.StudentNameAndId;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CoursesSelectedByStudentsGroupDTO {
    int selectiveCourseId;
    int semester;
    String courseName;
    String fieldOfKnowledgeCode;
    String trainingCycle;
    List<StudentNameAndId> students;
}
