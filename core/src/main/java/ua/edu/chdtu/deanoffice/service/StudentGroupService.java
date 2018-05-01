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

    public List<StudentGroup> getGroupsByCourse(int courseId, int facultyId) {
        return studentGroupRepository.findAllByCourse(courseId, facultyId);
    }

    public List<StudentGroup> getGraduateGroups(Integer degreeId, int facultyId) {
        Integer currYear = currentYearRepository.findOne(1).getCurrYear();
        return studentGroupRepository.findGraduateByDegree(degreeId, currYear, facultyId);
    }

    public List<StudentGroup> getGroupsByDegreeAndYear(int degreeId, int year) {
        Integer currYear = currentYearRepository.findOne(1).getCurrYear();
        return studentGroupRepository.findGroupsByDegreeAndYear(degreeId, year, currYear);
    }

    public List<StudentGroup> getAllByActive(boolean onlyActive) {
        if (onlyActive) {
            return this.studentGroupRepository.findAllActiveByFaculty(FACULTY_ID);
        }
        return this.studentGroupRepository.findAllByFaculty(FACULTY_ID);
    }

    public StudentGroup save(StudentGroup studentGroup) {
        return studentGroupRepository.save(studentGroup);
    }
}
