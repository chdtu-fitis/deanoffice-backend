package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SelectiveCoursesSelectionRulesDTO {
    private String typeCycle;
    private Integer[] selectiveCoursesNumber;
}
