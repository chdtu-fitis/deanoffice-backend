package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.repository.CourseForGroupRepository;
import ua.edu.chdtu.deanoffice.repository.GradeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CourseForGroupService {
    private final CourseForGroupRepository courseForGroupRepository;
    private final GradeRepository gradeRepository;
    private final GradeService gradeService;

    @Autowired
    public CourseForGroupService(CourseForGroupRepository courseForGroupRepository, GradeRepository gradeRepository,
                                 GradeService gradeService) {
        this.courseForGroupRepository = courseForGroupRepository;
        this.gradeRepository = gradeRepository;
        this.gradeService = gradeService;
    }

    public CourseForGroup getCourseForGroup(int id) {
        return courseForGroupRepository.findOne(id);
    }

    public List<CourseForGroup> getCoursesForGroups(int[] ids) {
        return courseForGroupRepository.findByIds(ids);
    }

    public List<CourseForGroup> getCoursesForOneGroup(int idGroup) {
        return courseForGroupRepository.findAllByStudentGroupId(idGroup);
    }

    public CourseForGroup getCourseForGroup(int groupId, int courseId) {
        return courseForGroupRepository.findByStudentGroupIdAndCourseId(groupId, courseId);
    }

    public List<CourseForGroup> getCoursesForGroupBySemester(int idGroup, int semester) {
        return courseForGroupRepository.findAllByStudentGroupIdAndCourseSemester(idGroup, semester);
    }

    public List<CourseForGroup> getCourseForGroupBySpecialization(int specialization, int semester) {
        return courseForGroupRepository.findAllBySpecialization(specialization, semester);
    }

    public List<CourseForGroup> getCoursesForGroupBySemester(int semester) {
        return courseForGroupRepository.findAllBySemester(semester);
    }

    public boolean areGradesForCourseForGroups(List<Integer> courseForGroupIds) {
        return courseForGroupIds
                .stream()
                .anyMatch(courseForGroupRepository::areGradesFor);
    }

    public void validateDeleteCourseForGroups(List<Integer> courseForGroupsIds) throws Exception {
        if (areGradesForCourseForGroups(courseForGroupsIds)) {
            throw new OperationCannotBePerformedException(
                    "Неможливо видалити предмет, якщо хоч в одного студента є оцінка з предмету, що видаляється."
            );
        }
    }

    @Transactional
    public void addCourseForGroupAndNewChanges(
            Set<CourseForGroup> newCourses,
            Map<Boolean, Set<CourseForGroup>> updatedCourses,
            List<Integer> deleteCoursesIds
    ) throws UnauthorizedFacultyDataException {
        checkFacultyAccessBeforeStoring(updatedCourses, deleteCoursesIds);
        courseForGroupRepository.save(newCourses);
        saveUpdatedCoursesForGroup(updatedCourses);
        for (Integer courseId : deleteCoursesIds) {
            courseForGroupRepository.delete(courseId);
        }
    }

    private void checkFacultyAccessBeforeStoring(Map<Boolean, Set<CourseForGroup>> updatedCourses,
                                                 List<Integer> deleteCoursesIds) throws UnauthorizedFacultyDataException {
        for (Map.Entry<Boolean, Set<CourseForGroup>> entry : updatedCourses.entrySet()) {
            Set<CourseForGroup> coursesForGroup = entry.getValue();
            checkFacultyAccess(new ArrayList<>(coursesForGroup));
        }
        List<CourseForGroup> coursesForGroupForDelete = courseForGroupRepository.findAll(deleteCoursesIds);
        checkFacultyAccess(coursesForGroupForDelete);
    }

    private void checkFacultyAccess(List<CourseForGroup> coursesForGroup) throws UnauthorizedFacultyDataException {
        for (CourseForGroup courseForGroup : coursesForGroup) {
            if (courseForGroup.getStudentGroup().getSpecialization().getFaculty().getId() != FacultyUtil.getUserFacultyIdInt())
                throw new UnauthorizedFacultyDataException();
        }
    }

    private void saveUpdatedCoursesForGroup(Map<Boolean, Set<CourseForGroup>> courses){
        Set<CourseForGroup> coursesWithChangedAcademicDifference = courses.get(true);
        Set<CourseForGroup> coursesLessChangedAcademicDifference = courses.get(false);
        for (CourseForGroup courseForGroup: coursesWithChangedAcademicDifference){
            gradeRepository.updateAcademicDifferenceByCourseIdAndGroupId(courseForGroup.isAcademicDifference(), courseForGroup.getStudentGroup().getId(), courseForGroup.getCourse().getId());
        }
        courseForGroupRepository.save(coursesWithChangedAcademicDifference);
        courseForGroupRepository.save(coursesLessChangedAcademicDifference);
    }

    @Transactional
    public void updateCourseInCoursesForGroupsAndGrade(CourseForGroup courseForGroup, Course newCourse, int oldCourseId, int groupId, int oldKnowledgeControlId) {
        courseForGroup.setCourse(newCourse);
        save(courseForGroup);
        List<Grade> grades = gradeService.getGradesByCourseAndGroup(oldCourseId, groupId);
        Map<String, Boolean> gradedChange = gradeService.evaluateGradedChange(oldKnowledgeControlId, newCourse.getKnowledgeControl().getId());
        gradeService.saveGradesByCourse(newCourse, grades, gradedChange);
    }

    public void save(CourseForGroup courseForGroup) {
        this.courseForGroupRepository.save(courseForGroup);
    }

    public boolean hasSoleCourse(int courseId) {
        int count = courseForGroupRepository.countByCourseId(courseId);
        if (count == 1)
            return true;
        else
            return false;
    }

    public void updateCourseIdById(int newId, int oldId) {
        courseForGroupRepository.updateCourseIdByCourseId(newId, oldId);
    }
}
