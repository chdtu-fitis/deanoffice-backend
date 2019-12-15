package ua.edu.chdtu.deanoffice.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.Payment;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.repository.CurrentYearRepository;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        return studentGroupRepository.findGraduateByDegree(degreeId, getCurrentYear(), facultyId);
    }

    private int getCurrentYear() {
        return currentYearRepository.findOne(1).getCurrYear();
    }

    public List<StudentGroup> getGroupsByDegreeAndYear(int degreeId, int year, int facultyId) {
        return studentGroupRepository.findGroupsByDegreeAndYear(degreeId, year, getCurrentYear(), facultyId);
    }

    public List<StudentGroup> getGroupsByDegreeAndYearAndTuitionForm(int degreeId, int year, int facultyId, TuitionForm tuitionForm ) {
        return studentGroupRepository.findGroupsByDegreeAndYearAndTuitionForm(degreeId, year, getCurrentYear(), facultyId, tuitionForm);
    }

    public List<StudentGroup> getAllByActive(boolean active, int facultyId) {
        return this.studentGroupRepository.findByActiveAndFaculty(active, facultyId);
    }

    public List<StudentGroup> getAllGroups(boolean onlyActive) {
        if (onlyActive) {
            return this.studentGroupRepository.findAllActive();
        }
        return this.studentGroupRepository.findAll();
    }

    public StudentGroup save(StudentGroup studentGroup) {
        return studentGroupRepository.save(studentGroup);
    }

    public List<StudentGroup> getByIds(List<Integer> groupIds) {
        return studentGroupRepository.findAllByIds(groupIds);
    }

    public List<StudentGroup> getByIdsAndByActive(List<Integer> groupsIds, boolean active) {
        return studentGroupRepository.findAllByIdsAndActive(groupsIds, active);
    }

    public void delete(List<StudentGroup> studentGroups) {
        studentGroups.forEach(studentGroup -> studentGroup.setActive(false));
        studentGroupRepository.save(studentGroups);
    }

    public void restore(List<StudentGroup> studentGroups) {
        studentGroups.forEach(studentGroup -> studentGroup.setActive(true));
        studentGroupRepository.save(studentGroups);
    }
  
    public List<StudentGroup> getGroupsThatAreStudyingSameCourseTo(Integer courseIds, Integer facultyId, Integer degreeId) {
        return studentGroupRepository.findGroupsThatAreStudyingSameCourseTo(courseIds, facultyId, degreeId);
    }

    public Map<Integer, List<StudentGroup>> getGroupsThatAreStudyingSameCoursesTo(List<Integer> courseIds, Integer facultyId, Integer degreeId) {
        Map<Integer, List<StudentGroup>> map = new HashMap<>();
        courseIds.forEach(courseId -> map.put(courseId, studentGroupRepository.findGroupsThatAreStudyingSameCourseTo(courseId, facultyId, degreeId)));
        return map;
    }

    public StudentGroup getByNameAndFacultyId(String groupName, int facultyId){
        List<StudentGroup> studentGroups = studentGroupRepository.findByName(groupName, facultyId);
        return (studentGroups.isEmpty()) ? null : studentGroups.get(0);
    }

    @Transactional
    public void setStudentGroupsInactiveByIds(Set<Integer> ids) {
        studentGroupRepository.setStudentGroupInactiveByIds(ids);
    }

    public List<StudentGroup> getBySpecializationId(int specializationId){
        List<StudentGroup> studentGroups = studentGroupRepository.findBySpecializationId(specializationId);
        return (studentGroups.size() > 0) ? studentGroups : null;
    }

    public List<StudentGroup> getGroupsMatchingForeignGroups(Boolean active) {
        return studentGroupRepository.findStudentGroupsMatchingForeignGroups(active);
    }

    public List<StudentGroup> getGroupsBySelectionCriteria(Specification<StudentGroup> specification) {
        return studentGroupRepository.findAll(specification);
    }

    public List<Integer> getStudySemestersByIds(List<Integer> ids, boolean active) {
        return studentGroupRepository.findStudySemestersByIds(ids, active);
    }
}
