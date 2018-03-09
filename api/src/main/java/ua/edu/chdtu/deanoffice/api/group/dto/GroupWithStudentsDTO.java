package ua.edu.chdtu.deanoffice.api.group.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by user on 09.03.2018.
 */
@Getter
@Setter
public class GroupWithStudentsDTO {
    private int id;
    private String name;
    private List<StudDegreeFullNameDTO> studentDegrees;
}
