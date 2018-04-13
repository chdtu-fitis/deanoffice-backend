package ua.edu.chdtu.deanoffice.api.student.dto;

import lombok.Getter;
import ua.edu.chdtu.deanoffice.entity.EducationDocument;

import java.util.Date;

@Getter
public class PreviousDiplomaDTO {
    private Date date;
    private String number;
    private EducationDocument type;

    public PreviousDiplomaDTO(Date date, String number, EducationDocument type) {
        this.date = date;
        this.number = number;
        this.type = type;
    }

    public PreviousDiplomaDTO(EducationDocument type) {
        this.type = type;
    }
}
