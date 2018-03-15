package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;

import java.util.List;

@Service
public class GroupService {
    private StudentGroupRepository studentGroupRepository;

    public GroupService(StudentGroupRepository studentGroupRepository) {
        this.studentGroupRepository = studentGroupRepository;
    }

    public List<StudentGroup> getGroups() {
        return studentGroupRepository.findAllByFaculty(1);
    }

    public List<Student> getGroupStudents(Integer groupId) {
        return studentGroupRepository.findOne(groupId).getStudents();
    }
    //TODO потрібно прибрати
    public StudentGroup getGroup(Integer id) {
        return studentGroupRepository.findOne(id);
    }
}
