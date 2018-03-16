package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.CurrentYearRepository;
import ua.edu.chdtu.deanoffice.repository.GroupRepository;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;

import java.util.List;

@Service
public class GroupService {
    private StudentGroupRepository studentGroupRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private CurrentYearRepository currentYearRepository;

    public GroupService(StudentGroupRepository studentGroupRepository) {
        this.studentGroupRepository = studentGroupRepository;
    }

    public List<StudentGroup> getGroups() {
        return studentGroupRepository.findAllByFaculty(1);
    }

    public List<StudentDegree> getGroupStudents(Integer groupId) {
        return studentGroupRepository.findOne(groupId).getStudentDegrees();
    }

    public List<StudentGroup> getGroupsByYear(int year) {
        Integer currYear = currentYearRepository.findOne(1).getCurrYear();
        return groupRepository.findGroupsByYear(year, currYear);
    }
}
