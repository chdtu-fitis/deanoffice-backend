package ua.edu.chdtu.deanoffice.service.document.informal.gradesabstract.beans;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Grade;

import java.util.Date;

@Setter
@Getter
public class StudentGradeAbstractBean {

    public StudentGradeAbstractBean(Grade grade, Date date, boolean isSelective) {
        this.grade = grade;
        this.date = date;
        this.isSelective = isSelective;
    }

    private Grade grade;
    private Date date;
    private boolean isSelective;
}
