package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.PersonFullNameDTO;
import ua.edu.chdtu.deanoffice.api.specialization.dto.SpecializationDTO;
import ua.edu.chdtu.deanoffice.entity.Payment;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.entity.TuitionTerm;

@Getter
@Setter
public class StudentDegreeDTO {
    private Integer id;
    private PersonFullNameDTO student;
    private NamedDTO studentGroup;
    private String recordBookNumber;
    private Payment payment;
    private TuitionForm tuitionForm;
    private TuitionTerm tuitionTerm;
    private SpecializationDTO specialization;
}
