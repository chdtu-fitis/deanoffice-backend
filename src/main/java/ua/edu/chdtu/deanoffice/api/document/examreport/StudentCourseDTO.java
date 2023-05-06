package ua.edu.chdtu.deanoffice.api.document.examreport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentCourseDTO {
    private int studentDegreeId;
    private int[] courses;
}
