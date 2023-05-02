package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.DepartmentDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.FieldOfKnowledgeDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.teacher.TeacherDTO;
import ua.edu.chdtu.deanoffice.entity.TrainingCycle;
import java.util.List;

@Getter
@Setter
public class SelectiveCourseDTO {
    private Integer id;
    private boolean available;
    private CourseDTO course;
    private TeacherDTO teacher;
    private NamedDTO degree;
    private DepartmentDTO department;
    private List<FieldOfKnowledgeDTO> fieldsOfKnowledge;
    private TrainingCycle trainingCycle;
    private String description;
    private Integer studyYear;
    private String groupName;
}
