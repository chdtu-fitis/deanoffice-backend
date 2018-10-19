package ua.edu.chdtu.deanoffice.service.datasync.edebo.student;

import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public interface EdeboStudentDataSyncronizationService {

    boolean isCriticalDataAvailable(StudentDegree studentDegree);

    Student getStudentFromData(ImportedData data);

    Speciality getSpecialityFromData(ImportedData data);

    Specialization getSpecializationFromData(ImportedData data);

    StudentDegree getStudentDegreeFromData(ImportedData data);

    EdeboStudentDataSynchronizationReport getEdeboDataSynchronizationReport(InputStream xlsxInputStream, Map<String, String> selectionParams) throws Exception;

    void addSynchronizationReportForImportedData(ImportedData importedData,EdeboStudentDataSynchronizationReport edeboDataSyncronizationReport, Map<String, String> selectionParams);

    boolean isSecondaryFieldsMatch(StudentDegree studentDegreeFromFile, StudentDegree studentDegreeFromDb);
}
