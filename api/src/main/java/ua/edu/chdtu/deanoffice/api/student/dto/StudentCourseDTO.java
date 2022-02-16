package ua.edu.chdtu.deanoffice.api.student.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class StudentCourseDTO {
    int studentDegreeId;
    List<SemesterCourses> firstSemesterCourses;
    List<SemesterCourses> secondSemesterCourses;
}

@Getter
@Setter
class SemesterCourses {
    private String name;
    private int hours;
    private BigDecimal credits;
    private int semester;
    private String teacher;
    private String knowledgeControl;
}
