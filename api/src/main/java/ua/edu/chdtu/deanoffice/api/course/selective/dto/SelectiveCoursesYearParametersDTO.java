package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Data;

@Data
public class SelectiveCoursesYearParametersDTO extends BaseSelectiveCoursesYearParametersDTO {
    private int generalMinStudentsCount;
    private int professionalMinStudentsCount;
}
