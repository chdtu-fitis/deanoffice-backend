package ua.edu.chdtu.deanoffice.api.student.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;

import java.util.Date;

@Setter
@Getter
public class StudentAcademicVacationDTO {
    @JsonView(StudentView.AcademicVacation.class)
    private Integer id;
    @JsonView(StudentView.AcademicVacation.class)
    private StudentDegreeDTO studentDegree;
    @JsonView(StudentView.AcademicVacation.class)
    private Date vacationStartDate;
    @JsonView(StudentView.AcademicVacation.class)
    private Date vacationEndDate;
    @JsonView(StudentView.AcademicVacation.class)
    private String orderNumber;
    @JsonView(StudentView.AcademicVacation.class)
    private Date orderDate;
    @JsonView(StudentView.AcademicVacation.class)
    private NamedDTO reason;
    @JsonView(StudentView.AcademicVacation.class)
    private Date applicationDate;

    private NamedDTO studentGroup;
    private int studyYear;
    private String extraInformation;

    private Integer studentDegreeId;
    private Integer reasonId;
}
