package ua.edu.chdtu.deanoffice.api.report.averagegrades.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class StudentsAveragePointsForYearDTO {
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
    private BigDecimal averageGrade;
}
