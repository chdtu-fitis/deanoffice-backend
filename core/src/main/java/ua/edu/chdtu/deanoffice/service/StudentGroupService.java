package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;

import java.util.List;

@Service
public class StudentGroupService {

    private StudentGroupRepository studentGroupRepository;

    public StudentGroupService(StudentGroupRepository studentGroupRepository) {
        this.studentGroupRepository = studentGroupRepository;
    }

    public StudentGroup getById(Integer groupId){
        return studentGroupRepository.getOne(groupId);
    }

    public List<StudentGroup> findByFacultyId(Integer facultyId){
        return studentGroupRepository.findAllByFaculty(facultyId);
    }
}
