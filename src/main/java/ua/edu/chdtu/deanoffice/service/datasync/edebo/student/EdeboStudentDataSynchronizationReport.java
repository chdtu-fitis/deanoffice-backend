package ua.edu.chdtu.deanoffice.service.datasync.edebo.student;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.student.beans.StudentDegreePrimaryDataWithGroupBean;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.student.beans.UnmatchedSecondaryDataStudentDegreeBlueBean;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.student.beans.MissingPrimaryDataRedMessageBean;

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
@Setter
public class EdeboStudentDataSynchronizationReport {
    private List<StudentDegreePrimaryDataWithGroupBean> synchronizedStudentDegreesGreen;
    private List<UnmatchedSecondaryDataStudentDegreeBlueBean> unmatchedSecondaryDataStudentDegreesBlue;
    private List<StudentDegree> noSuchStudentOrSuchStudentDegreeInDbOrange;
    private List<MissingPrimaryDataRedMessageBean> missingPrimaryDataRed;
    private List<StudentDegreePrimaryDataWithGroupBean> absentInFileStudentDegreesYellow;

    public EdeboStudentDataSynchronizationReport() {
        synchronizedStudentDegreesGreen = new ArrayList<>();
        unmatchedSecondaryDataStudentDegreesBlue = new ArrayList<>();
        noSuchStudentOrSuchStudentDegreeInDbOrange = new ArrayList<>();
        missingPrimaryDataRed = new ArrayList<>();
        absentInFileStudentDegreesYellow = new ArrayList<>();
    }

    public void addSyncohronizedDegreeGreen(StudentDegreePrimaryDataWithGroupBean bean) {
        synchronizedStudentDegreesGreen.add(bean);
    }

    public void addUnmatchedSecondaryDataStudentDegreeBlue(StudentDegree studentDegreeFromData, StudentDegree studentDegreeFromDb) {
        unmatchedSecondaryDataStudentDegreesBlue.add(new UnmatchedSecondaryDataStudentDegreeBlueBean(studentDegreeFromData, studentDegreeFromDb));
    }

    public void addNoSuchStudentOrStudentDegreeInDbOrange(StudentDegree studentDegreeFromData) {
        noSuchStudentOrSuchStudentDegreeInDbOrange.add(studentDegreeFromData);
    }

    public void addMissingPrimaryDataRed(MissingPrimaryDataRedMessageBean bean) {
        missingPrimaryDataRed.add(bean);
    }

    public void addAbsentInFileStudentDegreeYellow(StudentDegreePrimaryDataWithGroupBean bean) {
        absentInFileStudentDegreesYellow.add(bean);
    }
}
