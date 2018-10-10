package ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public  class  TwoListDTO {
    private StudentDegreeFullEdeboDataDto[] changedSecondaryData;
    private StudentDegreeFullEdeboDataDto[] newStudents;
}
