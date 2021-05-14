package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ua.edu.chdtu.deanoffice.entity.TypeCycle;

@Data
@AllArgsConstructor
public class SelectiveCoursesSelectionRulesDTO {
    private TypeCycle cycleType;
    private Integer[] selectiveCoursesNumber;
}
