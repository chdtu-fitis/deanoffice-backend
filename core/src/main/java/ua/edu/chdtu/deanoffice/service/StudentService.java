package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    private StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student get(Integer id) {
        return studentRepository.findOne(id);
    }

    public List<Student> getByGroupAndActive(StudentGroup group, Boolean isActive) {
        return studentRepository.getByStudentGroupAndActiveOrderBySurnameAsc(group, isActive);
    }

    public List<Student> getByGroupIdAndActive(Integer id, Boolean isActive) {
        return studentRepository.getByStudentGroupIdAndActiveOrderBySurnameAsc(id, isActive);
    }


}
