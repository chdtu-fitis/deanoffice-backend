package ua.edu.chdtu.deanoffice.service.datasync.edebo.diploma.number.beans;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.diploma.number.DiplomaImportData;

@Getter
@Setter
public class DiplomaAndStudentSynchronizedDataBean {
    private String surname;
    private String name;
    private String patronimic;
    private String diplomaSeriesAndNumber;
    private String fullSpecialityName;
    private String groupName;
    private boolean honor;

    public DiplomaAndStudentSynchronizedDataBean(StudentDegree studentDegree, String diplomaSeriesAndNumber, String honor){
        Student student = studentDegree.getStudent();
        Speciality speciality = studentDegree.getSpecialization().getSpeciality();
        this.surname = student.getSurname();
        this.name = student.getName();
        this.patronimic = student.getPatronimic();
        this.diplomaSeriesAndNumber = diplomaSeriesAndNumber;
        this.fullSpecialityName = speciality.getName() + " " + speciality.getCode();
        this.groupName = studentDegree.getStudentGroup().getName();
        this.honor = (honor.equals("-")) ? false : true;
    }

    public DiplomaAndStudentSynchronizedDataBean(DiplomaImportData importData){
        this.surname = importData.getLastName();
        this.name = importData.getFirstName();
        this.patronimic = importData.getMiddleName();
        this.diplomaSeriesAndNumber = importData.getDocumentSeries() + " № " + importData.getDocumentNumber();
        this.fullSpecialityName = importData.getSpecialityName();
        this.groupName = "";
        this.honor = (importData.getAwardTypeId().equals("-")) ? false : true;
    }
}
