package ua.edu.chdtu.deanoffice.api.specialization.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.speciality.dto.SpecialityDTO;

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
    @JsonView(SpecializationView.WithDegreeAndSpeciality.class)
    private SpecialityDTO speciality;
    @JsonView(SpecializationView.WithDegreeAndSpeciality.class)
    private NamedDTO degree;
    private NamedDTO department;
    private String qualification;
    private String qualificationEng;
    private BigDecimal paymentFulltime;
    private BigDecimal paymentExtramural;
    @JsonView(SpecializationView.Extended.class)
    private String educationalProgramHeadName;
    private String educationalProgramHeadNameEng;
    private String educationalProgramHeadInfo;
    private String educationalProgramHeadInfoEng;
    private String certificateNumber;
    private Date certificateDate;

    private Integer specialityId;
    private Integer degreeId;
    private Integer departmentId;
}
