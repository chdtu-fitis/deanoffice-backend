package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.CurrentYearRepository;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;

import java.util.List;

import static ua.edu.chdtu.deanoffice.Constants.FACULTY_ID;

@Service
public class StudentGroupService {

    private final StudentGroupRepository studentGroupRepository;
    private final CurrentYearRepository currentYearRepository;

    public StudentGroupService(
            StudentGroupRepository studentGroupRepository,
            CurrentYearRepository currentYearRepository
    ) {
        this.studentGroupRepository = studentGroupRepository;
        this.currentYearRepository = currentYearRepository;
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
    public List<StudentGroup> getGroups() {
        return studentGroupRepository.findAllActiveByFaculty(FACULTY_ID);
    }

    public List<StudentGroup> getGroupsByDegreeAndYear(int degreeId, int year) {
        Integer currYear = currentYearRepository.findOne(1).getCurrYear();
        return studentGroupRepository.findGroupsByDegreeAndYear(degreeId, year, currYear);
    }

    public List<StudentGroup> getAllActive() {
        return this.studentGroupRepository.findAllActiveByFaculty(FACULTY_ID);
    }
}
