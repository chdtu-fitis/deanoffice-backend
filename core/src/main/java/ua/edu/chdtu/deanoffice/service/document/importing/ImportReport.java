package ua.edu.chdtu.deanoffice.service.document.importing;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ImportReport {
    private List<StudentDegree> insertData;
    private List<StudentDegree> updateData;
    private List<Student> failData;

    public ImportReport() {
        insertData = new ArrayList<>();
        failData = new ArrayList<>();
        updateData = new ArrayList<>();
    }

    public void insert(StudentDegree studentDegree) {
        insertData.add(studentDegree);
    }

    public void update(StudentDegree studentDegree) {
        updateData.add(studentDegree);
    }

    public void fail(Student student) {
        failData.add(student);
    }

    public void clear() {
        insertData.clear();
        failData.clear();
        updateData.clear();
    }


}
