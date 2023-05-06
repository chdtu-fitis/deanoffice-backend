package ua.edu.chdtu.deanoffice.service.report.averagegrades;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class StudentsAveragePointsForYearBean {
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

    public StudentsAveragePointsForYearBean(Integer id,
                                            String surname,
                                            String name,
                                            String patronimic,
                                            String degreeName,
                                            String groupName,
                                            int year,
                                            String tuitionTerm,
                                            String specialityCode,
                                            String specialityName,
                                            String specializationName,
                                            BigDecimal averageGrade) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.patronimic = patronimic;
        this.degreeName = degreeName;
        this.groupName = groupName;
        this.year = year;
        this.tuitionTerm = tuitionTerm;
        this.specialityCode = specialityCode;
        this.specialityName = specialityName;
        this.specializationName = specializationName;
        this.averageGrade = averageGrade;
    }
}
