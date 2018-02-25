package ua.edu.chdtu.deanoffice.api.studentDegree.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.entity.TuitionTerm;

import java.math.BigDecimal;

@Setter
@Getter
public class StudentGroupDTO {
    private SpecializationDTO specialization;
    private int creationYear;
    private TuitionForm tuitionForm;
    private TuitionTerm tuitionTerm;
    private int studySemesters;
    private BigDecimal studyYears;
    private int beginYears;
    private boolean active;
    private String name;
    private Integer id;
}
