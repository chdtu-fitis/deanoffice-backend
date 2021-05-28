package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Data;

import java.util.List;

@Data
public class SelectiveCoursesStudentDegreeExpellingDTO extends SelectiveCoursesStudentDegreeWithStudyYearDTO {
    private List<Integer> selectiveCoursesInsteadOfExpelled;
}
