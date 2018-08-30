package ua.edu.chdtu.deanoffice.service.document.importing;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ImportReport {
    private List<StudentDegree> matchedStudentDegrees;
    private List<List<StudentDegree>> unmatchedStudentDegrees;
    private List<StudentDegree> noSuchStudentAndStudentDegrees;
    private List<StudentDegree> noSuchStudentDegrees;
    private List<StudentDegree> incompleteStudentDegrees;
    private List<StudentDegree> absentInFileStudentDegrees;

    public ImportReport() {
        matchedStudentDegrees = new ArrayList<>();
        unmatchedStudentDegrees = new ArrayList<>();
        noSuchStudentAndStudentDegrees = new ArrayList<>();
        noSuchStudentDegrees = new ArrayList<>();
        incompleteStudentDegrees = new ArrayList<>();
        absentInFileStudentDegrees = new ArrayList<>();
    }

    @PostConstruct
    private void initUnmatchedStudentDegreesList (){
        unmatchedStudentDegrees.add(new ArrayList<>());
        unmatchedStudentDegrees.add(new ArrayList<>());
    }

    public void addSyncohronizedDegrees(StudentDegree studentDegree) {
        matchedStudentDegrees.add(studentDegree);
    }

    public void addUnmatchedStudentDegrees(StudentDegree studentDegreeFromFile, StudentDegree studentDegreeFromDB){
        unmatchedStudentDegrees.get(0).add(studentDegreeFromFile);
        unmatchedStudentDegrees.get(1).add(studentDegreeFromDB);
    }

    public void addNoSuchStudentAndStudentDegrees(StudentDegree studentDegreeFromFile){
        noSuchStudentAndStudentDegrees.add(studentDegreeFromFile);
    }

    public void addNoSuchStudentDegrees(StudentDegree studentDegreeFromFile){
        noSuchStudentDegrees.add(studentDegreeFromFile);
    }

    public void addIncompleteStudentDegrees(StudentDegree studentDegreeFromFile){
        incompleteStudentDegrees.add(studentDegreeFromFile);
    }

    public void absentInFileStudentDegrees(List<StudentDegree> studentDegreesFromDB){
        absentInFileStudentDegrees.addAll(studentDegreesFromDB);
    }

    public void clear() {
        matchedStudentDegrees.clear();
        unmatchedStudentDegrees.clear();
        noSuchStudentAndStudentDegrees.clear();
        noSuchStudentDegrees.clear();
        incompleteStudentDegrees.clear();
        absentInFileStudentDegrees.clear();
    }


}
