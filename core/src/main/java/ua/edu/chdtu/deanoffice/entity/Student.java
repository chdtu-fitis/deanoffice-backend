package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import ua.edu.chdtu.deanoffice.entity.superclasses.Person;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "photo")
    private byte[] photo;
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<StudentDegree> degrees = new HashSet<>();
}
