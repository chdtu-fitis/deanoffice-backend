package ua.edu.chdtu.deanoffice.entity;

import ua.edu.chdtu.deanoffice.entity.superclasses.Person;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Student extends Person {
    @Column(name="surname_eng", length = 20)
    private String surnameEng;
    @Column(name="name_eng", length = 20)
    private String nameEng;
    @Column(name="patronimic_eng", length = 20)
    private String patronimicEng;
    @ManyToOne
    @JoinColumn(name="studentgroup_id")
    private StudentGroup studentGroup;
    @Column(name="birth_date")
    private Date birthDate;
    @Column(name="registration_address", length = 100)
    private String registrationAddress;
    @Column(name="actual_address", length = 100)
    private String actualAddress;
    @Column(name="school", length = 100)
    private String school;
    @Column(name="record_book_number", length = 15)
    private String recordBookNumber;
    @Column(name="student_card_number", length = 15)
    private String studentCardNumber;
    @Column(name="telephone", length = 30)
    private String telephone;
    @Column(name="email", length = 30)
    private String email;
    @ManyToOne
    private Privilege privilege;
    @Column(name="father_name", length = 40)
    private String fatherName;
    @Column(name="father_phone", length = 20)
    private String fatherPhone;
    @Column(name="father_info", length = 70)
    private String fatherInfo;
    @Column(name="mother_name", length = 40)
    private String motherName;
    @Column(name="mother_phone", length = 20)
    private String motherPhone;
    @Column(name="mother_info", length = 70)
    private String motherInfo;
    @Column(name="notes", length = 150)
    private String notes;

    public String getSurnameEng() {
        return surnameEng;
    }

    public void setSurnameEng(String surnameEng) {
        this.surnameEng = surnameEng;
    }

    public String getNameEng() {
        return nameEng;
    }

    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
    }

    public String getPatronimicEng() {
        return patronimicEng;
    }

    public void setPatronimicEng(String patronimicEng) {
        this.patronimicEng = patronimicEng;
    }

    public StudentGroup getStudentGroup() {
        return studentGroup;
    }

    public void setStudentGroup(StudentGroup studentGroup) {
        this.studentGroup = studentGroup;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getRegistrationAddress() {
        return registrationAddress;
    }

    public void setRegistrationAddress(String registrationAddress) {
        this.registrationAddress = registrationAddress;
    }

    public String getActualAddress() {
        return actualAddress;
    }

    public void setActualAddress(String actualAddress) {
        this.actualAddress = actualAddress;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getRecordBookNumber() {
        return recordBookNumber;
    }

    public void setRecordBookNumber(String recordBookNumber) {
        this.recordBookNumber = recordBookNumber;
    }

    public String getStudentCardNumber() {
        return studentCardNumber;
    }

    public void setStudentCardNumber(String studentCardNumber) {
        this.studentCardNumber = studentCardNumber;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getFatherPhone() {
        return fatherPhone;
    }

    public void setFatherPhone(String fatherPhone) {
        this.fatherPhone = fatherPhone;
    }

    public String getFatherInfo() {
        return fatherInfo;
    }

    public void setFatherInfo(String fatherInfo) {
        this.fatherInfo = fatherInfo;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getMotherPhone() {
        return motherPhone;
    }

    public void setMotherPhone(String motherPhone) {
        this.motherPhone = motherPhone;
    }

    public String getMotherInfo() {
        return motherInfo;
    }

    public void setMotherInfo(String motherInfo) {
        this.motherInfo = motherInfo;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
