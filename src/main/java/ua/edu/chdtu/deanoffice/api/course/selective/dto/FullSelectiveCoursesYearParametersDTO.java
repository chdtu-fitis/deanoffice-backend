package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Data;

@Data
public class FullSelectiveCoursesYearParametersDTO extends BaseSelectiveCoursesYearParametersDTO {
    private int bachelorGeneralMinStudentsCount;
    private int bachelorProfessionalMinStudentsCount;
    private int masterGeneralMinStudentsCount;
    private int masterProfessionalMinStudentsCount;
    private int phdGeneralMinStudentsCount;
    private int phdProfessionalMinStudentsCount;
    private int maxStudentsCount;
}
