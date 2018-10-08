package ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AllListsDTO {
    List<MissingPrimaryDataRedDTO> missingPrimaryDataRed;
    List<StudentDegreePrimaryEdeboDataDTO> synchronizedStudentDegreesGreen;
    List<StudentDegreePrimaryEdeboDataDTO> absentInFileStudentDegreesYellow;
    List<StudentDegreeFullEdeboDataDto> unmatchedSecondaryDataStudentDegreesBlue;
    List<StudentDegreeFullEdeboDataDto> noSuchStudentOrSuchStudentDegreeInDbOrange;
}