package ua.edu.chdtu.deanoffice.service.datasync.edebo.student;

import ua.edu.chdtu.deanoffice.entity.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public interface EdeboStudentDataSyncronizationService {

    boolean isCriticalDataAvailable(StudentDegree studentDegree);

    Student getStudentFromData(ImportedData data);

    Speciality getSpecialityFromData(ImportedData data);

    Specialization getSpecializationFromData(ImportedData data);

    StudentPreviousUniversity getStudentPreviousUniversityFromData(ImportedData data);

    StudentDegree getStudentDegreeFromData(ImportedData data);

    EdeboStudentDataSynchronizationReport getEdeboDataSynchronizationReport(InputStream xlsxInputStream, int facultyId, Map<String, String> selectionParams) throws Exception;

    void addSynchronizationReportForImportedData(ImportedData importedData,EdeboStudentDataSynchronizationReport edeboDataSyncronizationReport, int facultyId, Map<String, String> selectionParams);

    boolean isSecondaryFieldsMatch(StudentDegree studentDegreeFromFile, StudentDegree studentDegreeFromDb);
}
