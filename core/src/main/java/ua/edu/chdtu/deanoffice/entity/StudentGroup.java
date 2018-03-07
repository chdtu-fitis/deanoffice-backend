package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithActiveEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Table(name = "student_group")
public class StudentGroup extends NameWithActiveEntity {
    @ManyToOne
    private Specialization specialization;
    @Column(name = "creation_year", nullable = false)
    private int creationYear;
    @Column(name = "tuition_form", nullable = false, length = 10, columnDefinition = "varchar(10) default 'FULL_TIME'")
    @Enumerated(value = EnumType.STRING)
    private TuitionForm tuitionForm = TuitionForm.FULL_TIME;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "tuition_term", nullable = false, length = 10, columnDefinition = "varchar(10) default 'REGULAR'")
    private TuitionTerm tuitionTerm = TuitionTerm.REGULAR;
    @Column(name = "study_semesters", nullable = false)
    private int studySemesters;
    @Column(name = "study_years", nullable = false)
    private BigDecimal studyYears;
    @Column(name = "begin_years", nullable = false)
    private int beginYears;//курс, з якого починає навчатись група
    @OneToMany(mappedBy = "studentGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<StudentDegree> studentDegrees = new HashSet<>();
    //CURATOR

    public List<Student> getStudents() {
        if (studentDegrees.isEmpty()) {
            return new ArrayList<>();
        } else {
            return studentDegrees.stream().map(StudentDegree::getStudent).collect(Collectors.toList());
        }
    }
}
