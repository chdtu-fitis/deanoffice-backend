package ua.edu.chdtu.deanoffice.entity;

import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithActiveEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "student_group")
public class StudentGroup extends NameWithActiveEntity {
    @ManyToOne
    private Specialization specialization;
    @Column(name = "creation_year", nullable = false)
    private int creationYear;
    @Column(name = "tuition_form", nullable = false)
    private char tuitionForm = 'f';//f - fulltime, e - extramural
    //TODO cr: замініть на енум - ніяких констант і магічних слів
    @Column(name = "tuition_term", nullable = false)
    private char tuitionTerm = 'r';//r - regular, s - shortened
    //TODO cr: замініть на енум - ніяких констант і магічних слів
    @Column(name = "study_semesters", nullable = false)
    private int studySemesters;
    @Column(name = "study_years", nullable = false)
    private BigDecimal studyYears;
    @Column(name = "begin_years", nullable = false)
    private int beginYears;//курс, з якого починає навчатись група
    @OneToMany(mappedBy = "studentGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Student> students;
    //CURATOR

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    public int getBeginYears() {
        return beginYears;
    }

    public void setBeginYears(int beginYears) {
        this.beginYears = beginYears;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public int getCreationYear() {
        return creationYear;
    }

    public void setCreationYear(int creationYear) {
        this.creationYear = creationYear;
    }

    public char getTuitionForm() {
        return tuitionForm;
    }

    public void setTuitionForm(char tuitionForm) {
        this.tuitionForm = tuitionForm;
    }

    public char getTuitionTerm() {
        return tuitionTerm;
    }

    public void setTuitionTerm(char tuitionTerm) {
        this.tuitionTerm = tuitionTerm;
    }

    public int getStudySemesters() {
        return studySemesters;
    }

    public void setStudySemesters(int studySemesters) {
        this.studySemesters = studySemesters;
    }

    public BigDecimal getStudyYears() {
        return studyYears;
    }

    public void setStudyYears(BigDecimal studyYears) {
        this.studyYears = studyYears;
    }
}
