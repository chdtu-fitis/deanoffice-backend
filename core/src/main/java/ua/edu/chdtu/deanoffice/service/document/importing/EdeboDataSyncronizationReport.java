package ua.edu.chdtu.deanoffice.service.document.importing;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


@Getter
public class EdeboDataSyncronizationReport {
    private List<StudentDegree> synchronizedStudentDegreesGreen;
    private List<List<StudentDegree>> unmatchedSecondaryDataStudentDegreesBlue;
    private List<StudentDegree> noSuchStudentOrSuchStudentDegreeInDbOrange;
    private List<ImportedData> missingPrimaryDataRed;
    private List<StudentDegree> absentInFileStudentDegreesYellow;

    public EdeboDataSyncronizationReport() {
        synchronizedStudentDegreesGreen = new ArrayList<>();
        unmatchedSecondaryDataStudentDegreesBlue = new ArrayList<>();
        unmatchedSecondaryDataStudentDegreesBlue.add(new ArrayList<>());
        unmatchedSecondaryDataStudentDegreesBlue.add(new ArrayList<>());
        noSuchStudentOrSuchStudentDegreeInDbOrange = new ArrayList<>();
        missingPrimaryDataRed = new ArrayList<>();
        absentInFileStudentDegreesYellow = new ArrayList<>();
    }

    public void addSyncohronizedDegreeGreen(StudentDegree studentDegree) {
        synchronizedStudentDegreesGreen.add(studentDegree);
    }

    public void addUnmatchedSecondaryDataStudentDegreeBlue(StudentDegree studentDegreeFromData, StudentDegree studentDegreeFromDb){
        unmatchedSecondaryDataStudentDegreesBlue.get(0).add(studentDegreeFromData);
        unmatchedSecondaryDataStudentDegreesBlue.get(1).add(studentDegreeFromDb);
    }

    public void addNoSuchStudentOrStudentDegreeInDbOrange(StudentDegree studentDegreeFromData){
        noSuchStudentOrSuchStudentDegreeInDbOrange.add(studentDegreeFromData);
    }

    public void addMissingPrimaryDataRed(ImportedData importedData){
        missingPrimaryDataRed.add(importedData);
    }

    public void addAbsentInFileStudentDegreeYellow(List<StudentDegree> studentDegreesFromDB){
        absentInFileStudentDegreesYellow.addAll(studentDegreesFromDB);
    }

    public void clear() {
        synchronizedStudentDegreesGreen.clear();
        unmatchedSecondaryDataStudentDegreesBlue.clear();
        noSuchStudentOrSuchStudentDegreeInDbOrange.clear();
        missingPrimaryDataRed.clear();
        absentInFileStudentDegreesYellow.clear();
    }


}
