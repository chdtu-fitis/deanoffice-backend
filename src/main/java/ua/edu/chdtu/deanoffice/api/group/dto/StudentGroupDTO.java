package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.specialization.dto.SpecializationDTO;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.entity.TuitionTerm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StudentGroupDTO {
    @JsonView({StudentGroupView.Basic.class, StudentGroupView.Short.class})
    private Integer id;
    @JsonView({StudentGroupView.Basic.class, StudentGroupView.Short.class})
    private String name;
    @JsonView(StudentGroupView.AllGroupData.class)
    private boolean active;
    @JsonView(StudentGroupView.AllGroupData.class)
    private int studySemesters;
    @JsonView(StudentGroupView.Basic.class)
    private int creationYear;
    @JsonView(StudentGroupView.WithStudents.class)
    private List<StudentDegreeFullNameDTO> studentDegrees = new ArrayList<>();
    @JsonView({StudentGroupView.AllGroupData.class, StudentGroupView.WithStudents.class})
    private SpecializationDTO specialization;
    @JsonView(StudentGroupView.Basic.class)
    private TuitionForm tuitionForm;
    @JsonView(StudentGroupView.AllGroupData.class)
    private TuitionTerm tuitionTerm;
    @JsonView(StudentGroupView.AllGroupData.class)
    private BigDecimal studyYears;
    @JsonView(StudentGroupView.AllGroupData.class)
    private int realBeginYear;
    @JsonView(StudentGroupView.AllGroupData.class)
    private int beginYears;
    private int specializationId;
}
