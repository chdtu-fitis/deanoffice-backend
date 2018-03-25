package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.entity.TuitionTerm;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class StudentGroupDTO {
    @JsonView(StudentGroupView.Basic.class)
    private Integer id;
    @JsonView(StudentGroupView.Basic.class)
    private String name;
    private boolean active;
    private Specialization specialization;
    private int creationYear;
    private TuitionForm tuitionForm;
    private TuitionTerm tuitionTerm;
    private int studySemesters;
    private BigDecimal studyYears;
    private int beginYears;
    @JsonView(StudentGroupView.WithStudents.class)
    private List<StudentDegreeFullNameDTO> studentDegrees;
}