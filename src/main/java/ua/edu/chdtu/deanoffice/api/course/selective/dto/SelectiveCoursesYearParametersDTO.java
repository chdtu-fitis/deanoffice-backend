package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Data;
import ua.edu.chdtu.deanoffice.entity.PeriodCaseEnum;

@Data
public class SelectiveCoursesYearParametersDTO extends BaseSelectiveCoursesYearParametersDTO {
    private int generalMinStudentsCount;
    private int professionalMinStudentsCount;
    private int maxStudentsCount;
    private PeriodCaseEnum periodCase;
}
