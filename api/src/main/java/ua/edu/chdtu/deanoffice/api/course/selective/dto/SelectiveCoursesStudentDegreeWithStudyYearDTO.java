package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SelectiveCoursesStudentDegreeWithStudyYearDTO extends SelectiveCoursesStudentDegreeWriteDTO {
    @NotNull
    private int studyYear;
}
