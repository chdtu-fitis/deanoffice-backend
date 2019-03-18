package ua.edu.chdtu.deanoffice.api.student.synchronization.diploma.number.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FormedListsWithDiplomaDataDTO {
    List<DiplomaAndStudentSynchronizedDataDTO> diplomaAndStudentSynchronizedDataDTOs;
    List<MissingDataRedDTO> missingDataRedDTOs;
}
