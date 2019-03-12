package ua.edu.chdtu.deanoffice.api.course.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;

import java.util.List;

@Getter
@Setter
public class ForeignStudentsSynchronizationDTO {
    private List<CourseDTO> common;
    private List<CourseDTO> differentForeignCourses;
    private List<CourseDTO> differentOtherCourses;
    private NamedDTO otherGroup;
    private NamedDTO foreignGroup;
}
