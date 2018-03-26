package ua.edu.chdtu.deanoffice.api.student.dto;

import ua.edu.chdtu.deanoffice.entity.EducationDocument;

import java.util.Date;

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

    public String getNumber() {
        return number;
    }

    public Date getDate() {
        return date;
    }

    public EducationDocument getType() {
        return type;
    }
}
