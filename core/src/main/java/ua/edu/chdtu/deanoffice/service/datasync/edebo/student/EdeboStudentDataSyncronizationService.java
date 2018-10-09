package ua.edu.chdtu.deanoffice.service.datasync.edebo.student;

import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import java.io.InputStream;

public interface EdeboStudentDataSyncronizationService {

    boolean isCriticalDataAvailable(StudentDegree studentDegree);

    Student getStudentFromData(ImportedData data);

    Speciality getSpecialityFromData(ImportedData data);

    Specialization getSpecializationFromData(ImportedData data);

    StudentDegree getStudentDegreeFromData(ImportedData data);

    EdeboStudentDataSynchronizationReport getEdeboDataSynchronizationReport(InputStream xlsxInputStream, int facultyId) throws Exception;

    void addSynchronizationReportForImportedData(ImportedData importedData,EdeboStudentDataSynchronizationReport edeboDataSyncronizationReport, int facultyId);

    boolean isSecondaryFieldsMatch(StudentDegree studentDegreeFromFile, StudentDegree studentDegreeFromDb);
}
