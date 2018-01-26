package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.repository.StudentRepository;

import java.util.List;

@Service
public class GroupService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentGroupRepository studentGroupRepository;

    public List<StudentGroup> getGroups() {
        List<StudentGroup> studentGroups = studentGroupRepository.findAllByFaculty(1);
        return studentGroups;
    }

    public List<Student> getGroupStudents(Integer groupId) {
        return studentRepository.findAllByStudentGroupIdAndActiveOrderBySurnameAscNameAscPatronimicAsc(groupId, true);
    }
}
