package ua.edu.chdtu.deanoffice.api.student.stipend;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StudentInfoForStipendDTO {
    private Integer id;
    private String surname;
    private String name;
    private String patronimic;
    private int year;
    private String specializationName;
    private String departmentAbbreviation;
    private double averageGrade;
    private Integer extraPoints;
    List<CourseForStipendDTO> debtCourses = new ArrayList<>();

    public double getFinalGrade() {
        if (extraPoints != null)
            return averageGrade * 0.9 + extraPoints;
        else
            return averageGrade * 0.9;
    }
}
