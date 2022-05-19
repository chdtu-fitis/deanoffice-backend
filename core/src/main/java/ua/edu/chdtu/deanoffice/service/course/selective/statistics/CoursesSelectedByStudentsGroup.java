package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CoursesSelectedByStudentsGroup {
    int selectiveCourseId;
    int studentDegreeId;
    int semester;
    String fieldOfKnowledgeCode;
    String trainingCycle;
    String courseName;
    List<StudentNameAndId> students;

    public CoursesSelectedByStudentsGroup(int selectiveCourseId, int studentDegreeId, int semester, String courseName, String trainingCycle, String fieldOfKnowledgeCode) {
        this.selectiveCourseId = selectiveCourseId;
        this.studentDegreeId = studentDegreeId;
        this.semester = semester;
        this.courseName = courseName;
        this.fieldOfKnowledgeCode = fieldOfKnowledgeCode;
        this.trainingCycle = trainingCycle;
    }

    public CoursesSelectedByStudentsGroup() {
    }
}
