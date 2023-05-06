package ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentsNotRegisteredForSelectiveCoursesDTO {
    private String name;
    private String faculty;
    private String specialityCode;
    private String groupName;
    private String department;
}
