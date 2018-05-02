package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.CurrentYearRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;

import java.util.List;

import static ua.edu.chdtu.deanoffice.Constants.FACULTY_ID;

@Service
public class StudentGroupService {

    private final StudentGroupRepository studentGroupRepository;
    private final CurrentYearRepository currentYearRepository;
    private final StudentDegreeRepository studentDegreeRepository;

    public StudentGroupService(
            StudentGroupRepository studentGroupRepository,
            CurrentYearRepository currentYearRepository,
            StudentDegreeRepository studentDegreeRepository
    ) {
        this.studentGroupRepository = studentGroupRepository;
        this.currentYearRepository = currentYearRepository;
        this.studentDegreeRepository = studentDegreeRepository;
    }

    public StudentGroup getById(Integer studentGroupId) {
        return this.studentGroupRepository.findOne(studentGroupId);
    }

    public List<StudentGroup> getGroupsByCourse(int courseId) {
        return studentGroupRepository.findAllByCourse(courseId);
    }

    public List<StudentGroup> getGraduateGroups(Integer degreeId) {
        Integer currYear = currentYearRepository.findOne(1).getCurrYear();
        return studentGroupRepository.findGraduateByDegree(degreeId, currYear);
    }

    public List<StudentGroup> getGroupsByDegreeAndYear(int degreeId, int year) {
        Integer currYear = currentYearRepository.findOne(1).getCurrYear();
        return studentGroupRepository.findGroupsByDegreeAndYear(degreeId, year, currYear);
    }

    public List<StudentGroup> getAllByActive(boolean onlyActive, int facultyId) {
        if (onlyActive) {
            return this.studentGroupRepository.findAllActiveByFaculty(facultyId);
        }
        return this.studentGroupRepository.findAllByFaculty(facultyId);
    }
}
