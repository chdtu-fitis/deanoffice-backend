package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithActiveEntity;
import ua.edu.chdtu.deanoffice.util.comparators.StudentDegreeFullNameComparator;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
public class StudentGroup extends NameWithActiveEntity {
    @ManyToOne
    private Specialization specialization;
    private int creationYear;
    @Enumerated(value = EnumType.STRING)
    private TuitionForm tuitionForm = TuitionForm.FULL_TIME;
    @Enumerated(value = EnumType.STRING)
    private TuitionTerm tuitionTerm = TuitionTerm.REGULAR;
    private int studySemesters;
    private BigDecimal studyYears;
    private int beginYears;
    @OneToMany(mappedBy = "studentGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Where(clause = "active = true")
    private List<StudentDegree> studentDegrees = new ArrayList<>();

    public List<StudentDegree> getStudentDegrees() {
        studentDegrees.sort(new StudentDegreeFullNameComparator());
        return studentDegrees;
    }

    public List<Student> getActiveStudents() {
        if (studentDegrees.isEmpty()) {
            return new ArrayList<>();
        } else {
            return getStudentDegrees().stream().map(StudentDegree::getStudent).collect(Collectors.toList());
        }
    }
}
