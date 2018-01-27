package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithActiveEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Getter
@Setter
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
}
