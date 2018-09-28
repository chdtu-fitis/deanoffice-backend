package ua.edu.chdtu.deanoffice.service.document.importing;

import lombok.Getter;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.document.importing.beans.StudentDegreePrimaryDataBean;

import java.util.ArrayList;
import java.util.List;

/**
 * SD - StudentDegree
 * This class is created for containing and processing data from import and it contains
 * different lists.
 * Green - for fully synchronized SD;
 * Blue - secondary data (secondary data means one, that is not used for entity identification) doesn't match;
 * Orange - there is no student in db -> no such SD for this student at all, or there is some SD with
 * particular student, but not with the imported data;
 * Red - missing key values in data, or the program can't cast date / specialization patterns doesn't match;
 * Yellow - SD that are present in db, but aren't in imported data
 */
// add yellow case
@Getter
public class EdeboStudentDataSyncronizationReport {
    private List<StudentDegreePrimaryDataBean> synchronizedStudentDegreesGreen;
    private List<List<StudentDegree>> unmatchedSecondaryDataStudentDegreesBlue;
    private List<StudentDegree> noSuchStudentOrSuchStudentDegreeInDbOrange;
    private List<StudentDegreePrimaryDataBean> missingPrimaryDataRed;
    private List<StudentDegreePrimaryDataBean> absentInFileStudentDegreesYellow;

    public EdeboStudentDataSyncronizationReport() {
        synchronizedStudentDegreesGreen = new ArrayList<>();
        unmatchedSecondaryDataStudentDegreesBlue = new ArrayList<>();
        unmatchedSecondaryDataStudentDegreesBlue.add(new ArrayList<>());
        unmatchedSecondaryDataStudentDegreesBlue.add(new ArrayList<>());
        noSuchStudentOrSuchStudentDegreeInDbOrange = new ArrayList<>();
        missingPrimaryDataRed = new ArrayList<>();
        absentInFileStudentDegreesYellow = new ArrayList<>();
    }

    public void addSyncohronizedDegreeGreen(StudentDegreePrimaryDataBean bean) {
        synchronizedStudentDegreesGreen.add(bean);
    }

    public void addUnmatchedSecondaryDataStudentDegreeBlue(StudentDegree studentDegreeFromData, StudentDegree studentDegreeFromDb) {
        unmatchedSecondaryDataStudentDegreesBlue.get(0).add(studentDegreeFromData);
        unmatchedSecondaryDataStudentDegreesBlue.get(1).add(studentDegreeFromDb);
    }

    public void addNoSuchStudentOrStudentDegreeInDbOrange(StudentDegree studentDegreeFromData) {
        noSuchStudentOrSuchStudentDegreeInDbOrange.add(studentDegreeFromData);
    }

    public void addMissingPrimaryDataRed(StudentDegreePrimaryDataBean bean ) {
        missingPrimaryDataRed.add(bean);
    }

    public void addAbsentInFileStudentDegreeYellow(StudentDegreePrimaryDataBean bean) {
        absentInFileStudentDegreesYellow.add(bean);
    }

    public void clear() {
        synchronizedStudentDegreesGreen.clear();
        unmatchedSecondaryDataStudentDegreesBlue.clear();
        noSuchStudentOrSuchStudentDegreeInDbOrange.clear();
        missingPrimaryDataRed.clear();
        absentInFileStudentDegreesYellow.clear();
    }

//    public StudentDegreePrimaryDataDto getKeyValuesStudentDegreeForFront(StudentDegree sdData){
//        Student stud = sdData.getStudent();
//        Specialization specialization = sdData.getSpecialization();
//        Speciality speciality = sdData.getSpecialization().getSpeciality();
//        return new StudentDegreePrimaryDataDto(stud.getSurname(), stud.getName(),
//                stud.getPatronimic(), stud.getBirthDate(), sdData.getSpecialization().getDegree().getName(), speciality.getName(),
//                (specialization.getCode().equals("")) ? specialization.getName() : specialization.getCode() + " " + specialization.getName() );
//    }
//
//    public StudentDegreePrimaryDataBean getKeyValuesImportedData(ImportedData importedData){
//        return new StudentDegreePrimaryDataBean(importedData.getLastName(),importedData.getFirstName(),importedData.getMiddleName(),
//                importedData.getBirthday(),importedData.getQualificationGroupName(),importedData.getFullSpecialityName(),
//                importedData.getFullSpecializationName(),importedData.getProgramName());
//    }
//
//    public FullDbDataCrossedStudentDegreeDto getFullDbDataCrossedStudentDegreeForFront(StudentDegree sdData){
//        Student stud = sdData.getStudent();
//        Specialization specialization = sdData.getSpecialization();
//        Speciality speciality = sdData.getSpecialization().getSpeciality();
//        return new FullDbDataCrossedStudentDegreeDto(stud.getSurname(), stud.getName(),
//                stud.getPatronimic(), stud.getBirthDate(), sdData.getSpecialization().getDegree().getName(), speciality.getName(),
//                (specialization.getCode().equals("")) ? specialization.getName() : specialization.getCode() + " " + specialization.getName(),
//                sdData.getDiplomaNumber(),sdData.getPayment(),sdData.getPreviousDiplomaDate(), sdData.getPreviousDiplomaType(), sdData.getSupplementNumber(),
//                sdData.getAdmissionDate(),stud.getSurnameEng(),stud.getNameEng(),stud.getPatronimicEng());
//    }


}
