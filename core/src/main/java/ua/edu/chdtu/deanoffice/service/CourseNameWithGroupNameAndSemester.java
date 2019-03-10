package ua.edu.chdtu.deanoffice.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseNameWithGroupNameAndSemester {
    private String name;
    private String nameEng;
    private String groupName;
    private Integer semester;

    public CourseNameWithGroupNameAndSemester(String name, String nameEng,
                                              String groupName, Integer semester) {
        this.name = name;
        this.nameEng = nameEng;
        this.groupName = groupName;
        this.semester = semester;
    }


}

