package ua.edu.chdtu.deanoffice.service.document.report.journal;

import ua.edu.chdtu.deanoffice.entity.CourseForGroup;

import java.util.Comparator;

public class ComparatorCourses implements Comparator<CourseForGroup> {
    @Override
    public int compare(CourseForGroup o1, CourseForGroup o2) {
        if (o1.getExamDate() != null && o2.getExamDate() != null) {
            if (!o1.getExamDate().equals(o2.getExamDate()))
                if (o1.getExamDate().before(o2.getExamDate()))
                    return -1;
                else
                    return 1;
            if (o1.getCourse().getKnowledgeControl().getId() != o2.getCourse().getKnowledgeControl().getId())
                return o1.getCourse().getKnowledgeControl().getId() - o2.getCourse().getKnowledgeControl().getId();
            return o1.getCourse().getCourseName().getName().compareTo(o2.getCourse().getCourseName().getName());
        } else if (o1.getExamDate() != null && o2.getExamDate() == null) {
            return -1;
        } else if (o1.getExamDate() == null && o2.getExamDate() != null) {
            return 1;
        } else if (o1.getExamDate() == null && o2.getExamDate() == null) {
            if (o1.getCourse().getKnowledgeControl().getId() != o2.getCourse().getKnowledgeControl().getId())
                return o1.getCourse().getKnowledgeControl().getId() - o2.getCourse().getKnowledgeControl().getId();
            else
                return o1.getCourse().getCourseName().getName().compareTo(o2.getCourse().getCourseName().getName());
        }
        return 0;
    }
}
