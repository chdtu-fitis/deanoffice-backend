package ua.edu.chdtu.deanoffice.service.specialization;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompetenciesBean {
    private int id;
    private List<CompetenceBean> competencies;
    private int specializationId;
    private Integer year;

    public CompetenciesBean(){}

    public CompetenciesBean(int id, List<CompetenceBean> competencies, int specializationId, Integer year) {
        this.id = id;
        this.competencies = competencies;
        this.specializationId = specializationId;
        this.year = year;
    }
}
