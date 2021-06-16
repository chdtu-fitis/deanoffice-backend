package ua.edu.chdtu.deanoffice.api.specialization.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.FacultyDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedWithAbbrDTO;
import ua.edu.chdtu.deanoffice.api.speciality.dto.SpecialityDTO;
import ua.edu.chdtu.deanoffice.api.teacher.TeacherDTO;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class SpecializationDTO {
    @JsonView(SpecializationView.Basic.class)
    private int id;
    @JsonView(SpecializationView.Basic.class)
    private String name;
    @JsonView(SpecializationView.Extended.class)
    private String nameEng;
    @JsonView(SpecializationView.Extended.class)
    private boolean active;
    @JsonView({SpecializationView.WithDegreeAndSpeciality.class, SpecializationView.WithSpeciality.class})
    private SpecialityDTO speciality;
    @JsonView(SpecializationView.Basic.class)
    private NamedDTO degree;
    private NamedDTO department;
    private String code;
    private String qualification;
    private String qualificationEng;
    private BigDecimal paymentFulltime;
    private BigDecimal paymentExtramural;
    @JsonView(SpecializationView.Extended.class)
    private TeacherDTO programHead;
    private String educationalProgramHeadName;
    private String educationalProgramHeadNameEng;
    private String educationalProgramHeadInfo;
    private String educationalProgramHeadInfoEng;
    private String certificateNumber;
    private Date certificateDate;
    private String certificateIssuedBy;
    private String certificateIssuedByEng;

    private Integer specialityId;
    private Integer degreeId;
    private Integer departmentId;
    @JsonView(SpecializationView.Faculty.class)
    private Integer facultyId;
    @JsonView(SpecializationView.Faculty.class)
    private NamedWithAbbrDTO faculty;
}
