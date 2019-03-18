package ua.edu.chdtu.deanoffice.service.stipend;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Course;

import java.math.BigDecimal;
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
    private String specialityCode;
    private String specialityName;
    private String specializationName;
    private String departmentAbbreviation;
    private BigDecimal averageGrade;
    private String courseName;
    private String knowledgeControlName;
    private int semester;

    public DebtorStudentDegreesBean(Integer id,
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
                                    String departmentAbbreviation,
                                    BigDecimal averageGrade,
                                    String courseName,
                                    String knowledgeControlName,
                                    int semester) {
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
        this.departmentAbbreviation = departmentAbbreviation;
        this.averageGrade = averageGrade;
        this.courseName = courseName;
        this.knowledgeControlName = knowledgeControlName;
        this.semester = semester;
    }

    public DebtorStudentDegreesBean(Integer id,
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
                                    String departmentAbbreviation,
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
        this.departmentAbbreviation = departmentAbbreviation;
        this.averageGrade = averageGrade;
    }
}
