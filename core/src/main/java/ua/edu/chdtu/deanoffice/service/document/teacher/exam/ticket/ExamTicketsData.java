package ua.edu.chdtu.deanoffice.service.document.teacher.exam.ticket;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.Teacher;

@Getter
@Setter
public class ExamTicketsData {
    private Teacher teacher;
    private Course course;
    private Department department;
    private StudentGroup group;
    private String protocolNumber;
    private String protocolDate;

    public ExamTicketsData(Teacher teacher, Course course, Department department, StudentGroup group, String protocolNumber, String protocolDate) {
        this.teacher = teacher;
        this.course = course;
        this.department = department;
        this.group = group;
        this.protocolNumber = protocolNumber;
        this.protocolDate = protocolDate;
    }
}
