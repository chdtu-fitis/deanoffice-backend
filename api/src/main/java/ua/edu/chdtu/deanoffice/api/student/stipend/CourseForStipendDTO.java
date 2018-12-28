package ua.edu.chdtu.deanoffice.api.student.stipend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseForStipendDTO {
    private String courseName;
    private String knowledgeControlName;
    private int semester;

    public CourseForStipendDTO(String courseName, String knowledgeControlName, int semester) {
        this.courseName = courseName;
        this.knowledgeControlName = knowledgeControlName;
        this.semester = semester;
    }
}
