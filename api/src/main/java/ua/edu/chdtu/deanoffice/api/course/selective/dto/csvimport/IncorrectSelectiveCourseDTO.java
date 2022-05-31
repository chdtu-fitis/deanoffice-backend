package ua.edu.chdtu.deanoffice.api.course.selective.dto.csvimport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncorrectSelectiveCourseDTO extends SelectiveCourseCsvDTO {
    private String alert;
}
