package ua.edu.chdtu.deanoffice.courseforgroup.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupDTO {
    private int id;
    private String name;
    private int semesters;
    private int specializationId;

    GroupDTO(int id, String name, int semesters,int specializationId){
        this.id = id;
        this.name = name;
        this.semesters = semesters;
        this.specializationId = specializationId;
    }
}