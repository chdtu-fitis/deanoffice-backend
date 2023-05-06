package ua.edu.chdtu.deanoffice.api.student.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OneStudentGradeDTO {
    private String courseName;
    private int semester;
    private String knowledgeControl;
    private int points;
    private int grade;
}
