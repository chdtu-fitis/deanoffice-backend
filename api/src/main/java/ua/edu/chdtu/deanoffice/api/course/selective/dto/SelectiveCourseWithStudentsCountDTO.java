package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Data;

@Data
public class SelectiveCourseWithStudentsCountDTO extends SelectiveCourseDTO implements Comparable<SelectiveCourseWithStudentsCountDTO>{
    private int studentsCount;

    @Override
    public int compareTo(SelectiveCourseWithStudentsCountDTO o) {
        return this.studentsCount - o.getStudentsCount();
    }
}
