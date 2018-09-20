package ua.edu.chdtu.deanoffice.service.document.importing;

import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import java.io.InputStream;

public interface EdeboStudentDataSyncronizationService {

    boolean isCriticalDataAvailable(ImportedData data);

    Student getStudentFromData(ImportedData data);

    Speciality getSpecialityFromData(ImportedData data);

    Specialization getSpecializationFromData(ImportedData data);

    StudentDegree getStudentDegreeFromData(ImportedData data);

    EdeboDataSyncronizationReport getSyncronizationReport(InputStream xlsxInputStream);

    void addStudentDegreeToSyncronizationReport(StudentDegree studentDegree);

    boolean isSecondaryFieldsMatch(StudentDegree studentDegreeFromFile, StudentDegree studentDegreeFromDb);
}
