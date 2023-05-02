package ua.edu.chdtu.deanoffice.api.specialization.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class QualificationEventsDTO {
    private List<Integer> selected;
    private List<Integer> deleted;
}
