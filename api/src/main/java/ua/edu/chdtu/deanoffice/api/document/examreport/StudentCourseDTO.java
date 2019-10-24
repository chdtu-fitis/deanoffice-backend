package ua.edu.chdtu.deanoffice.api.document.examreport;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentCourseDTO {
    private int studentDegreeId;
    private int[] courses;
}
