package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Data;

@Data
public class SelectiveCourseWithStudentsCountDTO extends SelectiveCourseDTO {
    private Long studentsCount;
}
