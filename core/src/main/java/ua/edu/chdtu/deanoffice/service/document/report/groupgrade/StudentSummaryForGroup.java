package ua.edu.chdtu.deanoffice.service.document.report.groupgrade;

import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.StudentSummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentSummaryForGroup extends StudentSummary {


    public StudentSummaryForGroup(StudentDegree studentDegree, List<List<Grade>> grades) {
        super(studentDegree, grades);
    }

    public Map<String, String> getGeneralDictionary() {
        HashMap<String, String> result = new HashMap<>();
        result.put("student-name", getStudent().getInitialsUkr());
        return result;
    }


    public List<Course> getCourses() {
        List<Course> courses = new ArrayList<>();
        getGrades().get(0).forEach((grade) -> {
            courses.add(grade.getCourse());
        });
        return courses;
    }

    public Double getAverageGrade() {
        double gradeSum = 0;
        double gradeCount = 0;
        for (List<Grade> gradeList : getGrades()) {
            for (Grade grade : gradeList) {
                if (grade.getCourse().getKnowledgeControl().isGraded()&&grade.getGrade()!=null) {
                    gradeSum += grade.getGrade();
                    gradeCount++;
                }
            }
        }
        if (gradeCount == 0) {
            return 0.0;
        } else {
            return gradeSum / gradeCount;
        }
    }


    @Override
    protected Grade combineGrades(List<Grade> grades) {
        Grade result = super.combineGrades(grades);
        CombinedCourse newCourse = new CombinedCourse(result.getCourse());
        newCourse.setNumberOfSemesters(grades.size());
        newCourse.setStartingSemester(grades.get(0).getCourse().getSemester());
        if (newCourse.getSemester() == null) {
            newCourse.setSemester(grades.get(0).getCourse().getSemester());
        }
        result.setCourse((newCourse));
        return result;
    }
}
