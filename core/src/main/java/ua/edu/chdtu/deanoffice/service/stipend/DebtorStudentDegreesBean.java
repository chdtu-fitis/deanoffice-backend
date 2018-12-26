package ua.edu.chdtu.deanoffice.service.stipend;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Course;

import java.util.List;

@Getter
@Setter
public class DebtorStudentDegreesBean {
    private Integer id;
    private String surname;
    private String name;
    private String patronimic;
    private String degreeName;
    private String groupName;
    private int year;
    private String tuitionTerm;
    private String code;
    private String specialityName;
    private String specializationName;
    private String departmentAbbreviation;
    private double averageGrade;
    private List<Course> debtCourses;
}
