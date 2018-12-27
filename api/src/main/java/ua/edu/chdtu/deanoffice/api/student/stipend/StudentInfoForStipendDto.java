package ua.edu.chdtu.deanoffice.api.student.stipend;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentInfoForStipendDto {
    private Integer id;
    private String surname;
    private String name;
    private String patronimic;
    private String degreeName;
    private String groupName;
    private int year;
    private String tuitionTerm;
    private String specialityCode;
    private String specialityName;
    private String specializationName;
    private String departmentAbbreviation;
    private double averageGrade;
    List<CourseForStipendDto> debtCourses;
}
