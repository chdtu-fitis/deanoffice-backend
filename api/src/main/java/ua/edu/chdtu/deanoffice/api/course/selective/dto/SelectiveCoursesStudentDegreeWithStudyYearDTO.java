package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Data;

@Data
public class SelectiveCoursesStudentDegreeWithStudyYearDTO extends SelectiveCoursesStudentDegreeWriteDTO {
    private int studyYear;
}

