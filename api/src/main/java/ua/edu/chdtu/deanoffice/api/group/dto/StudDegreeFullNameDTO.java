package ua.edu.chdtu.deanoffice.api.group.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.PersonFullNameDTO;
import ua.edu.chdtu.deanoffice.entity.Student;

/**
 * Created by user on 09.03.2018.
 */
@Getter
@Setter
public class StudDegreeFullNameDTO {
    private int id;
    private PersonFullNameDTO student;
}
