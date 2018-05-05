package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.Person;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Student extends Person {
    private String surnameEng;
    private String nameEng;
    private String patronimicEng;
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    private String registrationAddress;
    private String actualAddress;
    private String school;
    private String telephone;
    private String email;
    @ManyToOne
    private Privilege privilege;
    private String fatherName;
    private String fatherPhone;
    private String fatherInfo;
    private String motherName;
    private String motherPhone;
    private String motherInfo;
    private String notes;
    private String photoUrl;
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<StudentDegree> degrees = new HashSet<>();
}
