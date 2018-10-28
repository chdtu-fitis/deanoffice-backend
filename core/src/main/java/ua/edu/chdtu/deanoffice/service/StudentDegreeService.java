package ua.edu.chdtu.deanoffice.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentDegreeService {
    private final StudentDegreeRepository studentDegreeRepository;

    public StudentDegreeService(StudentDegreeRepository studentDegreeRepository) {
        this.studentDegreeRepository = studentDegreeRepository;
    }

    public StudentDegree getById(Integer id) {
        return studentDegreeRepository.getById(id);
    }

    public List<StudentDegree> getAllByActive(boolean active, int facultyId) {
        return studentDegreeRepository.findAllByActive(active, facultyId);
    }

    public StudentDegree getFirst(Integer studentId) {
        List<StudentDegree> studentDegrees = this.studentDegreeRepository.findAllByStudentId(studentId);
        return (studentDegrees.isEmpty()) ? null : studentDegrees.get(0);
    }

    public List<StudentDegree> getAllByGroupId(Integer groupId) {
        return this.studentDegreeRepository.findStudentDegreeByStudentGroupIdAndActive(groupId, true);
    }

    public List<StudentDegree> getAllActiveByStudent(Integer studentId) {
        return this.studentDegreeRepository.findAllActiveByStudentId(studentId);
    }

    public StudentDegree getByStudentIdAndSpecializationId(boolean active,Integer studentId, Integer specializationId){
        return this.studentDegreeRepository.findByStudentIdAndSpecialityId(active,studentId,specializationId);
    }

    public StudentDegree save(StudentDegree studentDegree) {
        return this.studentDegreeRepository.save(studentDegree);
    }

    public void update(List<StudentDegree> studentDegree) {
        studentDegreeRepository.save(studentDegree);
    }

    public List<StudentDegree> getAllNotInImportData(List<Integer> ids, int facultyId){
        return studentDegreeRepository.findAll(StudentDegreeSpecification.getAbsentStudentDegreeInImportData(ids, facultyId));
    }

}
