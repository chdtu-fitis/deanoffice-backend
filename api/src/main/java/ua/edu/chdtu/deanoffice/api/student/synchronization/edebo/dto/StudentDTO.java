package ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.Sex;

import java.util.Date;

@Getter
@Setter
public class StudentDTO {
    private int id;
    private String name;
    private String surname;
    private String patronimic;
    private String nameEng;
    private String surnameEng;
    private String patronimicEng;
    private Sex sex;
    @JsonFormat(pattern="yyyy-MM-dd", locale = "uk_UA", timezone = "EET")
    private Date birthDate;
}
