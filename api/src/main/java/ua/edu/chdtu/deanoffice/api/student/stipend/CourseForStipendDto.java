package ua.edu.chdtu.deanoffice.api.student.stipend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseForStipendDto {
    private String courseName;
    private String knowledgeControlName;
    private int semester;
}
