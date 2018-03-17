package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.Person;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "student")
public class Student extends Person {
    @Column(name = "surname_eng", length = 20)
    private String surnameEng;
    @Column(name = "name_eng", length = 20)
    private String nameEng;
    @Column(name = "patronimic_eng", length = 20)
    private String patronimicEng;
    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    @Column(name = "registration_address", length = 100)
    private String registrationAddress;
    @Column(name = "actual_address", length = 100)
    private String actualAddress;
    @Column(name = "school", length = 100)
    private String school;
    @Column(name = "student_card_number", length = 15)
    private String studentCardNumber;
    @Column(name = "telephone", length = 30)
    private String telephone;
    @Column(name = "email", length = 30)
    private String email;
    @ManyToOne
    private Privilege privilege;
    @Column(name = "father_name", length = 40)
    private String fatherName;
    @Column(name = "father_phone", length = 20)
    private String fatherPhone;
    @Column(name = "father_info", length = 70)
    private String fatherInfo;
    @Column(name = "mother_name", length = 40)
    private String motherName;
    @Column(name = "mother_phone", length = 20)
    private String motherPhone;
    @Column(name = "mother_info", length = 70)
    private String motherInfo;
    @Column(name = "notes", length = 150)
    private String notes;
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<StudentDegree> degrees = new HashSet<>();

    public String getInitialsUkr() {
        return getSurname() + " " + getName().substring(0, 1) + " " + getPatronimic().substring(0, 1);
    }
}
