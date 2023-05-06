package ua.edu.chdtu.deanoffice.service.datasync.edebo.student.beans;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.student.ImportedData;

import java.text.SimpleDateFormat;

@Getter
@Setter
public class StudentDegreePrimaryDataBean {
    private Integer id;
    private String lastName;
    private String firstName;
    private String middleName;
    private String facultyName;
    private String birthday;
    private String degreeName;
    private String fullSpecialityName ;
    private String fullSpecializationName;

    public StudentDegreePrimaryDataBean(StudentDegree studentDegree){
        Student s = studentDegree.getStudent();
        Specialization sp = studentDegree.getSpecialization();
        this.id = studentDegree.getId();
        this.lastName=studentDegree.getStudent().getSurname();
        this.firstName=s.getName();
        this.middleName=s.getPatronimic();
        this.birthday=s.getBirthDate()==null? "" : new SimpleDateFormat("dd.MM.yyyy").format(s.getBirthDate());
        this.degreeName=studentDegree.getSpecialization().getDegree().getName();
        this.fullSpecialityName=sp.getSpeciality().getCode()+" "+sp.getSpeciality().getName();
        this.fullSpecializationName=sp.getName();
    }

    public StudentDegreePrimaryDataBean(ImportedData data) {
        this.lastName=data.getLastName();
        this.firstName=data.getFirstName();
        this.middleName= data.getMiddleName();
        this.birthday=data.getBirthday();
        this.degreeName=data.getQualificationGroupName();
        this.fullSpecialityName=data.getFullSpecialityName();
        this.fullSpecializationName=data.getProgramName();
    }


}
