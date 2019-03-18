package ua.edu.chdtu.deanoffice.api.student.synchronization.diploma.number.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiplomaAndStudentSynchronizedDataDTO {
    private int id;
    private String surname;
    private String name;
    private String patronimic;
    private String diplomaSeriesAndNumber;
    private String fullSpecialityName;
    private String groupName;
    private boolean honor;
}
