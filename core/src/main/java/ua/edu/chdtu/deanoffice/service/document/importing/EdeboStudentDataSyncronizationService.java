package ua.edu.chdtu.deanoffice.service.document.importing;

import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import java.util.List;

public interface EdeboStudentDataSyncronizationService {

    boolean isCriticalDataAvailable(ImportedData data);

    Student getStudentFromData(ImportedData data);

    Speciality getSpecialityFromData(ImportedData data);

    Specialization getSpecializationFromData(ImportedData data);

    StudentDegree getStudentDegreeFromData(ImportedData data);

    ImportReport getImportReport(List<ImportedData> importedData);

    void addStudentDegreeToSyncronizationReport(StudentDegree studentDegree);

    boolean isSecondaryFieldsMatch(StudentDegree studentDegreeFromFile, StudentDegree studentDegreeFromDb);
}
