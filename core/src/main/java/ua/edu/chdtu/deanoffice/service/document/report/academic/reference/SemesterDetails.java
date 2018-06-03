package ua.edu.chdtu.deanoffice.service.document.report.academic.reference;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Grade;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SemesterDetails {

    List<List<Grade>> grades;

    public SemesterDetails() {
        this.grades = new ArrayList<>();
        this.grades.add(new ArrayList<>());
        this.grades.add(new ArrayList<>());
        this.grades.add(new ArrayList<>());
        this.grades.add(new ArrayList<>());
    }
}
