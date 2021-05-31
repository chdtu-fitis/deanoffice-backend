package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Data;

import java.util.List;

@Data
public class SelectiveCoursesStudentDegreeSubstitutionDTO extends SelectiveCoursesStudentDegreeWithStudyYearDTO {
    private List<Integer> selectiveCoursesInsteadOfExpelled;
}
