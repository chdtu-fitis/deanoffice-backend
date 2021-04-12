package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Data;

@Data
public class SelectiveCourseWithStudentsCountDTO extends SelectiveCourseDTO implements Comparable<SelectiveCourseWithStudentsCountDTO>{
    private Integer studentsCount;

    @Override
    public int compareTo(SelectiveCourseWithStudentsCountDTO o) {
        return studentsCount.compareTo(o.getStudentsCount());
    }
}
