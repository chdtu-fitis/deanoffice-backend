package ua.edu.chdtu.deanoffice.api.group.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupWithStudentsDTO {
    private int id;
    private String name;
    private List<StudentDegreeFullNameDTO> studentDegrees;
}
