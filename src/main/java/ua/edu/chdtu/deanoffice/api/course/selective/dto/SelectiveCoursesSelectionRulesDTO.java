package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ua.edu.chdtu.deanoffice.entity.TrainingCycle;

@Data
@AllArgsConstructor
public class SelectiveCoursesSelectionRulesDTO {
    private TrainingCycle cycleType;
    private Integer[] selectiveCoursesNumber;
}
