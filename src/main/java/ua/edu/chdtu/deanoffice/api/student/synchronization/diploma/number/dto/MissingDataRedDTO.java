package ua.edu.chdtu.deanoffice.api.student.synchronization.diploma.number.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissingDataRedDTO {
    private String message;
    private DiplomaAndStudentSynchronizedDataDTO diplomaAndStudentSynchronizedDataBean;
}
