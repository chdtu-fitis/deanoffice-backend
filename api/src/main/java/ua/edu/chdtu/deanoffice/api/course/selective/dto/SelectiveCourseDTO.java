package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.DepartmentDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.teacher.TeacherDTO;
import ua.edu.chdtu.deanoffice.entity.TypeCycle;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    private List<NamedDTO> fieldsOfKnowledge;
    @Enumerated(EnumType.STRING)
    private TypeCycle trainingCycle;
    private String description;
    private Integer studyYear;
    private String groupName;
}
