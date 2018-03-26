package ua.edu.chdtu.deanoffice.api.general;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KnowledgeControlDTO {
    private int id;
    private String name;
    private String nameEng;
    private boolean hasGrade;
}
